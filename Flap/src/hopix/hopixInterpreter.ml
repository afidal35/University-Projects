open Position
open Error
open HopixAST

(** [error pos msg] reports execution error messages. *)
let error positions msg =
  errorN "execution" positions msg

(* [pattern_match_error pos] reports pattern matching error messages. *)
let pattern_matching_error pos = error [pos] "Pattern matching did not succeeded."

(** Every expression of Hopix evaluates into a [value].

   The [value] type is not defined here. Instead, it will be defined
   by instantiation of following ['e gvalue] with ['e = environment].
   Why? The value type and the environment type are mutually recursive
   and since we do not want to define them simultaneously, this
   parameterization is a way to describe how the value type will use
   the environment type without an actual definition of this type.

*)
type 'e gvalue =
  | VInt       of Mint.t
  | VChar      of char
  | VString    of string
  | VUnit
  | VTagged    of constructor * 'e gvalue list
  | VTuple     of 'e gvalue list
  | VRecord    of (label * 'e gvalue) list
  | VLocation  of Memory.location
  | VClosure   of 'e * pattern located * expression located
  | VPrimitive of string * ('e gvalue Memory.t -> 'e gvalue -> 'e gvalue)

(** Two values for booleans. *)
let ptrue  = VTagged (KId "True", [])
let pfalse = VTagged (KId "False", [])

(**
    We often need to check that a value has a specific shape.
    To that end, we introduce the following coercions. A
    coercion of type [('a, 'e)] coercion tries to convert an
    Hopix value into a OCaml value of type ['a]. If this conversion
    fails, it returns [None].
*)

type ('a, 'e) coercion = 'e gvalue -> 'a option
let fail = None
let ret x = Some x
let value_as_int      = function VInt x -> ret x | _ -> fail
let value_as_char     = function VChar c -> ret c | _ -> fail
let value_as_string   = function VString s -> ret s | _ -> fail
let value_as_tagged   = function VTagged (k, vs) -> ret (k, vs) | _ -> fail
let value_as_record   = function VRecord fs -> ret fs | _ -> fail
let value_as_location = function VLocation l -> ret l | _ -> fail
let value_as_closure  = function VClosure (e, p, b) -> ret (e, p, b) | _ -> fail
let value_as_primitive = function VPrimitive (p, f) -> ret (p, f) | _ -> fail
let value_as_bool = function
  | VTagged (KId "True", []) -> true
  | VTagged (KId "False", []) -> false
  | _ -> assert false

(**
   It is also very common to have to inject an OCaml value into
   the types of Hopix values. That is the purpose of a wrapper.
 *)
type ('a, 'e) wrapper = 'a -> 'e gvalue
let int_as_value x  = VInt x
let bool_as_value b = if b then ptrue else pfalse

let int64_from_option = function 
  | Some x -> x
  | None -> assert false

(**

  The flap toplevel needs to print the result of evaluations. This is
   especially useful for debugging and testing purpose. Do not modify
   the code of this function since it is used by the testsuite.

*)
let print_value m v =
  (** To avoid to print large (or infinite) values, we stop at depth 5. *)
  let max_depth = 5 in

  let rec print_value d v =
    if d >= max_depth then "..." else
      match v with
        | VInt x ->
          Mint.to_string x
        | VChar c ->
          "'" ^ Char.escaped c ^ "'"
        | VString s ->
          "\"" ^ String.escaped s ^ "\""
        | VUnit ->
          "()"
        | VLocation a ->
          print_array_value d (Memory.dereference m a)
        | VTagged (KId k, []) ->
          k
        | VTagged (KId k, vs) ->
          k ^ print_tuple d vs
        | VTuple (vs) ->
           print_tuple d vs
        | VRecord fs ->
           "{"
           ^ String.concat ", " (
                 List.map (fun (LId f, v) -> f ^ " = " ^ print_value (d + 1) v
           ) fs) ^ "}"
        | VClosure _ ->
          "<fun>"
        | VPrimitive (s, _) ->
          Printf.sprintf "<primitive: %s>" s
    and print_tuple d vs =
      "(" ^ String.concat ", " (List.map (print_value (d + 1)) vs) ^ ")"
    and print_array_value d block =
      let r = Memory.read block in
      let n = Mint.to_int (Memory.size block) in
      "[ " ^ String.concat ", " (
                 List.(map (fun i -> print_value (d + 1) (r (Mint.of_int i)))
                         (ExtStd.List.range 0 (n - 1))
               )) ^ " ]"
  in
  print_value 0 v

let print_values m vs =
  String.concat "; " (List.map (print_value m) vs)

module Environment : sig
  (** Evaluation environments map identifiers to values. *)
  type t

  (** The empty environment. *)
  val empty : t

  (** [bind env x v] extends [env] with a binding from [x] to [v]. *)
  val bind    : t -> identifier -> t gvalue -> t

  (** [update pos x env v] modifies the binding of [x] in [env] so
      that [x ↦ v] ∈ [env]. *)
  val update  : Position.t -> identifier -> t -> t gvalue -> unit

  (** [lookup pos x env] returns [v] such that [x ↦ v] ∈ env. *)
  val lookup  : Position.t -> identifier -> t -> t gvalue

  (** [UnboundIdentifier (x, pos)] is raised when [update] or
      [lookup] assume that there is a binding for [x] in [env],
      where there is no such binding. *)
  exception UnboundIdentifier of identifier * Position.t

  (** [last env] returns the latest binding in [env] if it exists. *)
  val last    : t -> (identifier * t gvalue * t) option

  (** [print env] returns a human readable representation of [env]. *)
  val print   : t gvalue Memory.t -> t -> string
end = struct

  type t =
    | EEmpty
    | EBind of identifier * t gvalue ref * t

  let empty = EEmpty

  let bind e x v =
    EBind (x, ref v, e)

  exception UnboundIdentifier of identifier * Position.t

  let lookup' pos x =
    let rec aux = function
      | EEmpty -> raise (UnboundIdentifier (x, pos))
      | EBind (y, v, e) ->
        if x = y then v else aux e
    in
    aux

  let lookup pos x e = !(lookup' pos x e)

  let update pos x e v =
    lookup' pos x e := v

  let last = function
    | EBind (x, v, e) -> Some (x, !v, e)
    | EEmpty -> None

  let print_binding m (Id x, v) =
    x ^ " = " ^ print_value m !v

  let print m e =
    let b = Buffer.create 13 in
    let push x v = Buffer.add_string b (print_binding m (x, v)) in
    let rec aux = function
      | EEmpty -> Buffer.contents b
      | EBind (x, v, EEmpty) -> push x v; aux EEmpty
      | EBind (x, v, e) -> push x v; Buffer.add_string b "\n"; aux e
    in
    aux e

end

(**
    We have everything we need now to define [value] as an instantiation
    of ['e gvalue] with ['e = Environment.t], as promised.
*)
type value = Environment.t gvalue

(**
   The following higher-order function lifts a function [f] of type
   ['a -> 'b] as a [name]d Hopix primitive function, that is, an
   OCaml function of type [value -> value].
*)
let primitive name ?(error = fun () -> assert false) coercion wrapper f
: value
= VPrimitive (name, fun x ->
    match coercion x with
      | None -> error ()
      | Some x -> wrapper (f x)
  )

type runtime = {
  memory      : value Memory.t;
  environment : Environment.t;
}

type observable = {
  new_memory      : value Memory.t;
  new_environment : Environment.t;
}

(** [primitives] is an environment that contains the implementation
    of all primitives (+, <, ...). *)
let primitives =
  let intbin name out op =
    let error m v =
      Printf.eprintf
        "Invalid arguments for `%s': %s\n"
        name (print_value m v);
      assert false (* By typing. *)
    in
    VPrimitive (name, fun m -> function
      | VInt x ->
         VPrimitive (name, fun m -> function
         | VInt y -> out (op x y)
         | v -> error m v)
      | v -> error m v)
  in
  let bind_all what l x =
    List.fold_left (fun env (x, v) -> Environment.bind env (Id x) (what x v))
      x l
  in
  (* Define arithmetic binary operators. *)
  let binarith name =
    intbin name (fun x -> VInt x) in
  let binarithops = Mint.(
    [ ("`+`", add); ("`-`", sub); ("`*`", mul); ("`/`", div) ]
  ) in
  (* Define arithmetic comparison operators. *)
  let cmparith name = intbin name bool_as_value in
  let cmparithops =
    [ ("`=?`", ( = ));
      ("`<?`", ( < ));
      ("`>?`", ( > ));
      ("`>=?`", ( >= ));
      ("`<=?`", ( <= )) ]
  in
  let boolbin name out op =
    VPrimitive (name, fun _ x -> VPrimitive (name, fun _ y ->
        out (op (value_as_bool x) (value_as_bool y))))
  in
  let boolarith name = boolbin name (fun x -> if x then ptrue else pfalse) in
  let boolarithops =
    [ ("`||`", ( || )); ("`&&`", ( && )) ]
  in
  let generic_printer =
    VPrimitive ("print", fun m v ->
      output_string stdout (print_value m v);
      flush stdout;
      VUnit
    )
  in
  let print s =
    output_string stdout s;
    flush stdout;
    VUnit
  in
  let print_int =
    VPrimitive  ("print_int", fun _ -> function
      | VInt x -> print (Mint.to_string x)
      | _ -> assert false (* By typing. *)
    )
  in
  let print_string =
    VPrimitive  ("print_string", fun _ -> function
      | VString x -> print x
      | _ -> assert false (* By typing. *)
    )
  in
  let bind' x w env = Environment.bind env (Id x) w in
  Environment.empty
  |> bind_all binarith binarithops
  |> bind_all cmparith cmparithops
  |> bind_all boolarith boolarithops
  |> bind' "print"        generic_printer
  |> bind' "print_int"    print_int
  |> bind' "print_string" print_string
  |> bind' "true"         ptrue
  |> bind' "false"        pfalse
  |> bind' "nothing"      VUnit

let initial_runtime () = {
  memory      = Memory.create (640 * 1024 (* should be enough. -- B.Gates *));
  environment = primitives;
}

exception Pattern_missmatch of Position.t

let rec evaluate runtime ast =
  try
    let runtime' = List.fold_left definition runtime ast in
    (runtime', extract_observable runtime runtime')
  with Environment.UnboundIdentifier (Id x, pos) ->
    Error.error "interpretation" pos (Printf.sprintf "`%s' is unbound." x)

(** [definition pos runtime d] evaluates the new definition [d]
    into a new runtime [runtime']. In the specification, this
    is the judgment:

                        E, M ⊢ dv ⇒ E', M'

*)
and definition runtime d =
  match Position.value d with
  | DefineType _ | DeclareExtern _ -> runtime
  | DefineValue x -> 
    match x with 
    | SimpleValue (id, _, e) -> 
      let v = expression' runtime.environment runtime.memory e in 
        { runtime with environment = bind_identifier runtime.environment id v }
    | RecFunctions fs -> 
      { runtime with environment = define_rec_functions runtime.environment fs }

and function_definition environment (FunctionDefinition(p,e)) =
  VClosure(environment, p , e)


  (** [define_rec_functions environment fs] extends [environment] with the bindings
    of mutually recursive functions [fs].  *)

and define_rec_functions environment fs =

  (* First, we bind all the function identifiers to a fake value (here
     VUnit).  *)

  let environment = 
    List.fold_left 
      (fun env (id, _, _) -> bind_identifier env id VUnit) environment fs 
  in 

  (* [eval_and_rebind (id,_,fd)] evaluates the function definition [fd]
     into a value [v] in the partially correct environment [environment]
     defined above, and then rebinds the function identifier [id] to [v]
     in [environment].  *)  

  let eval_and_rebind (id,_,fd) = 
    let id, pos = Position.destruct id in
    let v = (function_definition environment) fd in 
    Environment.update pos id environment v
  in 

  (* We then update the environment with the correct values. *)

  List.iter eval_and_rebind fs;

  (* Before returning the updated environment. *)

  environment

and expression' environment memory e =
  expression (position e) environment memory (value e)

(** [expression pos runtime e] evaluates into a value [v] if

                          E, M ⊢ e ⇓ v, M'

   and E = [runtime.environment], M = [runtime.memory].
*)

and expression position environment memory = function

  | Literal l -> Position.located literal l

  | Variable (id, _) -> 
      Position.(
        Environment.lookup (position id) (value id) environment
      )

  | Fun fd -> function_definition environment fd

  | Tagged (c, _, es) -> 
      let vs = expressions environment memory es in
      VTagged (Position.value c, vs)


  | Tuple e | Sequence e ->
      let v = expressions environment memory e in
      VTuple v

  | Record (le, _) ->
      let vs = record_expressions environment memory le in 
        VRecord vs 

  | Define (vd, e) ->
    begin match vd with
    | SimpleValue(id, _, e1) ->
      let v = expression' environment memory e1 in 
      let environment = bind_identifier environment id v in
        expression' environment memory e
    | RecFunctions fd -> 
      let environment = define_rec_functions environment fd in
        expression' environment memory e
    end

  | Field(e, l) ->
    let v = expression' environment memory e in 
    begin match value_as_record v with
    | Some lbe -> 
        let value = try List.assoc (Position.value l)  lbe with 
        | Not_found -> error [position] @@ "Field not found." ^ print_value memory v
        in value 
    | None -> assert false
    end
    
  | TypeAnnotation (e, _) -> expression' environment memory e

  | Ref e ->
      let v = expression' environment memory e in 
      let location = 
        try Memory.allocate memory Mint.one v with 
        | Memory.OutOfMemory -> error [position] "Out of memory." in
      VLocation location 

  | Read e ->
      let v = expression' environment memory e in
      begin match value_as_location v with 
      | Some addr -> 
          Memory.(read (dereference memory addr) Mint.zero)
      | None -> assert false 
      end
    
  | Assign (e1, e2) ->
      let v1 = expression' environment memory e1 in
      begin match value_as_location v1 with 
      | Some addr ->
          let v2 = expression' environment memory e2 in
          Memory.(write(dereference memory addr) Mint.zero v2);
          VUnit
      | None -> assert false
      end

  | Apply(e1, e2) ->
      let fv = expression' environment memory e1 in
      let vs = expression' environment memory e2 in 
        begin match fv with 
          (* If [fv] is a primitve then we evaluate vs in the [primitive] environment defined above. *)
          | VPrimitive (_, primitive) -> primitive memory vs
          (* In the other case it is a function call so we extend our environment with patterns then evaluate it.
             Reports an error message if the exception Pattern_missmatch is raised. *)
          | VClosure (environment, p, e) -> 
              let environment = 
                try pattern' environment vs p with
                | Pattern_missmatch pos -> pattern_matching_error pos
              in
              expression' environment memory e
          | _ -> assert false
        end
  
 | IfThenElse (e1, e2, e3) ->
      let fv = expression' environment memory e1 in
      expression' environment memory (if value_as_bool fv then e2 else e3)

  | For (id, e1 ,e2 ,e) ->
    let extract_int x =
      expression' environment memory x |>
      value_as_int |> int64_from_option |> Mint.to_int in
    for x = extract_int e1 to extract_int e2 do
      expression'
      (Environment.bind environment id.value
      (Mint.of_int x |> int_as_value)) memory e
      |> ignore ;
    done;
    VUnit
     
  | While(cond, body) ->
    while 
      value_as_bool (expression' environment memory cond)
    do 
      expression' environment memory body |> ignore;
    done;
    VUnit

  | Case (e, branches) -> 
      let v = expression' environment memory e in
      let rec aux = function
        | [] -> pattern_matching_error position
        | branch :: branches ->
        let Branch (p, e) = Position.value branch in
        (* Checking if the value [v] can be captured by the patern [p]
           We capture the Pattern_missmatch exception, then we go back to the recursion with the rest. *)
        try expression' (pattern' environment v p) memory e with
        | Pattern_missmatch _ -> aux branches
      in
      aux branches

(** Returns a list of [values] instead of a unique [value], usefull for expressions as Tuple, Sequence .. *)
and expressions environment memory es =  
  let rec aux vs = function 
    | [] -> List.rev vs
    | e :: es -> 
        let v = expression' environment memory e in
        aux (v :: vs) es 
  in 
  aux [] es

(** Returns a list of pairs [label, value], usefull for Record.
    Same principle as for [expressions environment memory es]. *)
and record_expressions environment memory es =
  List.fold_left (
    fun acc (l, e) -> 
      let v = expression' environment memory e in 
      (Position.value l, v) :: acc 
  ) [] es |> List.rev

and pattern' environment v p = 
      pattern (position p) environment v (value p)

    (** [pattern pos env v p] extends [env] so that the value [v] is captured by the pattern [p].

                               E, M ⊢ v ~ m ~> M'

    Raise exception Pattern_missmatch defined above if the value [v] cannot be captured by the pattern [p]. *) 

and pattern position environment v = function
  
  | PTypeAnnotation (p, _) -> pattern' environment v p

  | PVariable id -> bind_identifier environment id v

  | PLiteral l -> if Position.located literal l = v then environment else
      raise (Pattern_missmatch position)

  | PWildcard -> environment

  | PTaggedValue (c, _, ps) ->
    begin match value_as_tagged v with
    | Some (c',vs) ->
      if Position.value c = c' then patterns environment vs ps else
          raise (Pattern_missmatch position)
    | None -> assert false
    end

  | PTuple p ->
      begin match v with 
      | VTuple l -> patterns environment l p
      | _ -> assert false
      end

  | PRecord (lp, _) ->
      begin match value_as_record v with 
      | Some lp1 -> 
        let split, split' = List.split lp, List.split lp1 in 
        let lab_list1, lab_list2 = split |> fst , split' |> fst  in 
        let p_list, v_list = split |> snd , split' |> snd in 
          begin match List.for_all2 (fun a b -> Position.value a = b  ) lab_list1 lab_list2 with 
            | true -> patterns environment v_list p_list
            | false -> assert false
          end 
      | _ -> raise (Pattern_missmatch position)
      end

  | POr ps ->
      let rec aux = function
        | [] -> raise (Pattern_missmatch position)
        | p :: ps ->
            try pattern' environment v p with
            | Pattern_missmatch _ -> aux ps
        in
        aux ps

  | PAnd ps -> List.fold_left (fun env -> pattern' env v) environment ps

(** [patterns env vs ps] extends [env] in such a way that the values [vs] are 
    captured by the patterns [ps]. *)
and patterns environment vs ps =
  try List.fold_left2 pattern' environment vs ps with
  | Invalid_argument _ -> assert false

(* Shortcut for binding an identifier. *)
and bind_identifier environment x v =
  Environment.bind environment (Position.value x) v

and literal = function 
  | LInt x -> VInt x 
  | LString s -> VString s 
  | LChar c -> VChar c


(** This function returns the difference between two runtimes. *)
and extract_observable runtime runtime' =
  let rec substract new_environment env env' =
    if env == env' then new_environment
    else
      match Environment.last env' with
        | None -> assert false (* Absurd. *)
        | Some (x, v, env') ->
          let new_environment = Environment.bind new_environment x v in
          substract new_environment env env'
  in
  {
    new_environment =
      substract Environment.empty runtime.environment runtime'.environment;
    new_memory =
      runtime'.memory
  }

(** This function displays a difference between two runtimes. *)
let print_observable (_ : runtime) observation =
  Environment.print observation.new_memory observation.new_environment
