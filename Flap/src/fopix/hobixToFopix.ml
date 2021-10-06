(** This module implements a compiler from Hobix to Fopix. *)

(** As in any module that implements {!Compilers.Compiler}, the source
    language and the target language must be specified. *)

module Source = Hobix
module S = Source.AST
module Target = Fopix
module T = Target.AST

(**

   The translation from Hobix to Fopix turns anonymous
   lambda-abstractions into toplevel functions and applications into
   function calls. In other words, it translates a high-level language
   (like OCaml) into a first order language (like C).

   To do so, we follow the closure conversion technique.

   The idea is to make explicit the construction of closures, which
   represent functions as first-class objects. A closure is a block
   that contains a code pointer to a toplevel function [f] followed by all
   the values needed to execute the body of [f]. For instance, consider
   the following OCaml code:

   let f =
     let x = 6 * 7 in
     let z = x + 1 in
     fun y -> x + y * z

   The values needed to execute the function "fun y -> x + y * z" are
   its free variables "x" and "z". The same program with explicit usage
   of closure can be written like this:

   let g y env = env[1] + y * env[2]
   let f =
      let x = 6 * 7 in
      let z = x + 1 in
      [| g; x; z |]

   (in an imaginary OCaml in which arrays are untyped.)

   Once closures are explicited, there are no more anonymous functions!

   But, wait, how to we call such a function? Let us see that on an
   example:

   let f = ... (* As in the previous example *)
   let u = f 0

   The application "f 0" must be turned into an expression in which
   "f" is a closure and the call to "f" is replaced to a call to "g"
   with the proper arguments. The argument "y" of "g" is known from
   the application: it is "0". Now, where is "env"? Easy! It is the
   closure itself! We get:

   let g y env = env[1] + y * env[2]
   let f =
      let x = 6 * 7 in
      let z = x + 1 in
      [| g; x; z |]
   let u = f[0] 0 f

   (Remark: Did you notice that this form of "auto-application" is
   very similar to the way "this" is defined in object-oriented
   programming languages?)

*)

(**
   Helpers functions.
*)

let error pos msg =
  Error.error "compilation" pos msg

let make_fresh_variable =
  let r = ref 0 in
  fun () -> incr r; T.Id (Printf.sprintf "_%d" !r)


let make_fresh_function_identifier =
  let r = ref 0 in
  fun () -> incr r; T.FunId (Printf.sprintf "_%d" !r)

let define e f =
  let x = make_fresh_variable () in
  T.Define (x, e, f x)

let rec defines ds e =
  match ds with
    | [] -> e
    | (x, d) :: ds -> T.Define (x, d, defines ds e)

let seq a b =
  define a (fun _ -> b)

let rec seqs = function
  | [] -> assert false
  | [x] -> x
  | x :: xs -> seq x (seqs xs)

let allocate_block e =
  T.(FunCall (FunId "allocate_block", [e]))

let write_block e i v =
  T.(FunCall (FunId "write_block", [e; i; v]))

let read_block e i =
  T.(FunCall (FunId "read_block", [e; i]))

let lint i =
  T.(Literal (LInt (Int64.of_int i)))

(* "true" , "false" , "nothing". *)
let is_primitive (S.Id id) =
  FopixInterpreter.is_binary_primitive id || 
  List.mem id ["print_int"; "print_string"; "equal_string"; "equal_char"; "observe_int"]


(** [free_variables e] returns the list of free variables that
     occur in [e].*)
let free_variables =
  let module M =
    Set.Make (struct type t = S.identifier let compare = compare end)
  in
  let rec unions f = function
    | [] -> M.empty
    | [s] -> f s
    | s :: xs -> M.union (f s) (unions f xs)
  in
  let rec fvs = function
    | S.Literal _ ->
      M.empty

    | S.Variable x ->
      if is_primitive x then M.empty else M.singleton x

    | S.While (cond, e) ->
      unions fvs [cond; e]
      (*        Or          *)
      (* M.union (fvs cond) (fvs e) *)

    | S.Define (vd, a) ->
      let xs = 
        match vd with
        | S.SimpleValue(id, e) -> [(id, e)]
        | S.RecFunctions xs -> xs in 
        let ids, exs = List.split xs in 
        M.diff (unions fvs (a::exs)) (M.of_list ids)

    | S.ReadBlock (a, b) ->
       unions fvs [a; b]

    | S.Apply (a, b) ->
       unions fvs (a :: b)

    | S.WriteBlock (a, b, c) | S.IfThenElse (a, b, c) ->
       unions fvs [a; b; c]

    | S.AllocateBlock a ->
       fvs a

    | S.Fun (xs, e) ->
      M.diff (fvs e) (M.of_list xs)

    | S.Switch (a, b, c) ->
       let c = match c with None -> [] | Some c -> [c] in
       unions fvs (a :: ExtStd.Array.present_to_list b @ c)

  in
  fun e -> M.elements (fvs e)

(**

    A closure compilation environment relates an identifier to the way
    it is accessed in the compiled version of the function's
    body.

    Indeed, consider the following example. Imagine that the following
    function is to be compiled:

    fun x -> x + y

    In that case, the closure compilation environment will contain:

    x -> x
    y -> "the code that extract the value of y from the closure environment"

    Indeed, "x" is a local variable that can be accessed directly in
    the compiled version of this function's body whereas "y" is a free
    variable whose value must be retrieved from the closure's
    environment.

*)
type environment = {
    vars : (HobixAST.identifier, FopixAST.expression) Dict.t;
    externals : (HobixAST.identifier, int) Dict.t;
}

let initial_environment () =
  { vars = Dict.empty; externals = Dict.empty }

let bind_external id n env =
  { env with externals = Dict.insert id n env.externals }

let is_external id env =
  Dict.lookup id env.externals <> None

let bind_vars id n env =
  { env with vars = Dict.insert id n env.vars }

let reset_vars env =
   { env with vars = Dict.empty }

(** Precondition: [is_external id env = true]. *)
let arity_of_external id env =
  match Dict.lookup id env.externals with
    | Some n -> n
    | None -> assert false (* By is_external. *)

(** [translate p env] turns an Hobix program [p] into a Fopix program
    using [env] to retrieve contextual information. *)
let translate (p : S.t) env =
  let rec program env defs =
    let env, defs = ExtStd.List.foldmap definition env defs in
    (List.flatten defs, env)

  and definition env = function
    | S.DeclareExtern (id, n) ->
       let env = bind_external id n env in
       (env, [T.ExternalFunction (function_identifier id, n)])
    | S.DefineValue vd ->
       (env, value_definition env vd)

  and value_definition env = function
    | S.SimpleValue (x, e) ->
       let fs, e = expression (reset_vars env) e in
       fs @ [T.DefineValue (identifier x, e)]
    | S.RecFunctions fdefs ->
       let fs, defs = define_recursive_functions (reset_vars env) fdefs in
       fs @ List.map (fun (x, e) -> T.DefineValue (x, e)) defs

  and define_recursive_functions env rdefs =
    let rdefs = process_rdefs rdefs in

    let rec pre_allocate = function
    | [] -> []
    | fdef :: rdefs ->
      let def = match fdef with
        | (fid, _, _, free_var) ->
          let block_id = identifier fid in
          let block_name = T.Variable block_id in
          let block = create_block free_var in
          (block_id, T.Define(block_id, block, block_name))
        | _ -> assert false
      in
      def :: pre_allocate rdefs
    in 

    let rec aux = function
      | [] -> [], []
      | fdef :: rdefs -> 
        let f, def_expr = match fdef with
          | (fid, args, body, free_var) ->
            fid, (process_fun ~name_f:(identifier fid) env free_var args body)
          | _ -> assert false
        in
        let def, id_expr = (fst def_expr), [identifier f, (snd def_expr)] in
        let defs, id_exprs = aux rdefs in
        def@defs, id_expr @ id_exprs
    in
    let pre_def = pre_allocate rdefs in
    let defs, exprs = aux rdefs in
    defs, pre_def @ exprs

  and expression env = function

    | S.Literal l ->
      [], T.Literal (literal l)

    | S.While (cond, e) ->
       let cfs, cond = expression env cond in
       let efs, e = expression env e in
       cfs @ efs, T.While (cond, e)

    | S.Variable x ->
      let xc =
        if is_primitive x || is_external x env then
          T.Literal (T.LFun (function_identifier x))
        else match Dict.lookup x env.vars with
          | None -> T.Variable (identifier x)
          | Some e -> e
      in [], xc

    | S.Define (vdef, a) ->
        let afs, a = expression env a in 
        begin match vdef with
        | S.SimpleValue (id, b) ->
          let bfs, b = expression env b in 
          afs@bfs, T.Define (identifier id, b, a)
        | S.RecFunctions rdefs ->
          let f, defs = define_recursive_functions env rdefs in
          afs @ f, defines defs a
        | _ -> assert false
      end

    | S.Apply (a, bs) ->
      let process_primitive_args = function
        | S.Variable e when (is_primitive e) -> replace_primitive_pointer e (arity_of_primitive e)
        | e -> e
      in

      let bs = List.map process_primitive_args bs in
      let idfs, id = expression env a in
      let bsfs, es = expressions env bs in
      begin match id with
        (* If it is a known function, call it with it's identifier. *)
        | T.Literal (T.LFun fid) -> idfs@bsfs, T.FunCall (fid, es)
        (* If not, call it by reading the first block, where the function pointer is.  *)
        | e -> idfs@bsfs, T.UnknownFunCall (read_block e (lint 0), e::es)
      end

    | S.IfThenElse (a, b, c) ->
      let afs, a = expression env a in
      let bfs, b = expression env b in
      let cfs, c = expression env c in
      afs @ bfs @ cfs, T.IfThenElse (a, b, c)

    | S.Fun (x, e) as f ->
      process_fun env (free_variables f) x e

    | S.AllocateBlock a ->
      let afs, a = expression env a in
      afs, allocate_block a

    | S.WriteBlock (a, b, c) ->
      let afs, a = expression env a in
      let bfs, b = expression env b in
      let cfs, c = expression env c in
      afs @ bfs @ cfs, write_block a b c
      (*T.FunCall (T.FunId "write_block", [a; b; c])*)

    | S.ReadBlock (a, b) ->
      let afs, a = expression env a in
      let bfs, b = expression env b in
      afs @ bfs, read_block a b
      (*T.FunCall (T.FunId "read_block", [a; b])*)

    | S.Switch (a, bs, default) ->
      let afs, a = expression env a in
      let bsfs, bs =
        ExtStd.List.foldmap (fun bs t ->
                    match ExtStd.Option.map (expression env) t with
                    | None -> (bs, None)
                    | Some (bs', t') -> (bs @ bs', Some t')
                  ) [] (Array.to_list bs)
      in
      let dfs, default = match default with
        | None -> [], None
        | Some e -> let bs, e = expression env e in bs, Some e
      in
      afs @ bsfs @ dfs,
      T.Switch (a, Array.of_list bs, default)


  and expressions env = function
    | [] -> [], []
    | e :: es ->
       let efs, es = expressions env es in
       let fs, e = expression env e in
       fs @ efs, e :: es

  and literal = function
    | S.LInt x -> T.LInt x
    | S.LString s -> T.LString s
    | S.LChar c -> T.LChar c

  and identifier (S.Id x) = T.Id x

  and function_identifier (S.Id x) = T.FunId x  

  and lit_of_fid fid = T.Literal (T.LFun fid)

  and id_block T.(Literal (LFun (FunId id))) = T.Id ("block_" ^ id)

  and in_block_id T.(Literal (LFun (FunId id))) = T.Id ("env_" ^ id)

  and process_fun ?name_f env free_var args body = 
    let fid = make_fresh_function_identifier () in
    let args = List.map identifier args in
    let block, env = define_block env name_f fid free_var  in
    let defs = define_func env fid args body in
    defs, block

  and create_block free_var = 
    allocate_block (lint (List.length free_var + 1))

  and define_block (env : environment) name_f fid free_var =
    let block_id, block = match name_f with
      | Some name_f -> name_f, T.Variable name_f
      | None -> id_block (lit_of_fid fid), create_block free_var
    in
    let block_name = T.Variable block_id in
    let param_id = in_block_id (lit_of_fid fid) in
    let param_name = T.Variable param_id in 

    let pointer = write_block block_name (lint 0) (T.Literal (T.LFun fid)) in
    let instrs, env = closure_conversion env block_name param_name 1 free_var in
    let instrs = List.rev (block_name::pointer::instrs) |> seqs in
    T.Define (block_id, block, instrs), env

  and closure_conversion (env: environment) block above_block k = function
    | [] -> [], env
    | id :: ids -> 
      let new_env = bind_vars id (read_block above_block (lint k)) env in
      let tid = T.Variable (identifier id) in
      let tid =
        match Dict.lookup id env.vars with
        | None -> tid
        | Some e -> e 
      in
      let instr = write_block block (lint k) tid in
      let instrs, new_env = closure_conversion new_env block above_block (k+1) ids in
      (instr::instrs, new_env)

  and define_func (env: environment) fid args body =
    let defs, body = expression env body in
    defs@[T.DefineFunction (fid, (in_block_id (lit_of_fid fid))::args, body)]

  and process_rdefs = function
    | [] -> []
    | def :: rdefs -> 
      let fid = fst def in
      let args, body, free_var = match (snd def) with
        | S.Fun(args, body) as f -> 
          let free_var = free_variables f in
          args, body, free_var
        | _ -> assert false
      in
      (fid, args, body, free_var) :: process_rdefs rdefs

  and arity_of_primitive = function
    | (S.Id id) ->
          match id with
          | "print_string" | "print_int" | "observe_int" -> 1
          | "equal_char" | "equal_string" -> 2
          | s when FopixInterpreter.is_binary_primitive s -> 2
          | _ -> assert false
    | _ -> assert false

  and replace_primitive_pointer id arity =
    let rec aux = function
      | 0 -> []
      | k -> (S.Id ("arg_" ^ string_of_int k)) :: aux (k-1)
    in
    let args = aux arity in 
    let call_args = List.map (fun x -> S.Variable x) args in
    S.Fun (args, S.Apply (S.Variable id , call_args))
  in
  program env p
