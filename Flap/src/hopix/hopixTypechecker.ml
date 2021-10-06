(** This module implements a type checker for Hopix. *)
open HopixAST
open HopixTypes

let initial_typing_environment = HopixTypes.initial_typing_environment

type typing_environment = HopixTypes.typing_environment

let type_error = HopixTypes.type_error

let located f x = f (Position.position x) (Position.value x)

let rec variables_of_pattern' p = variables_of_pattern (Position.value p)

(* [variables_of_patern p] retrieve a (id, pos) list of the variables binded in the pattern [p]. *)
and variables_of_pattern = function
  | PTypeAnnotation (p, _) -> variables_of_pattern' p
  | PVariable x -> [Position.value x , Position.position x]
  | PTaggedValue (_,_, ps) | PAnd ps ->
      List.fold_left (fun acc p -> variables_of_pattern' p @ acc) [] ps
  | PRecord (lp,_) ->  List.fold_left (fun acc p -> variables_of_pattern' p @ acc) [] (List.map (fun (_,p) -> p) lp)
  | PTuple ps -> List.fold_left (fun acc p -> variables_of_pattern' p @ acc) [] ps
  | POr (p :: _) -> variables_of_pattern' p (* Environments in POr should bind the same values. *)
  | POr [] -> assert false
  | PWildcard | PLiteral _ -> []

(* Our module List, containing the default module List and our find_duplicate function. *)
module List = struct
  include List
  let find_duplicate list = 
    let rec dupExistAux lst b =
      match lst with
      | [] -> raise Not_found
      | hd::tl -> 
        if (List.exists (fun x -> x = hd) b) then hd
        else dupExistAux tl (hd::b) in
    dupExistAux list []
end;;

(* Print functions to visualize processes on Tagged, Record. *)
let string_from_aty_list list = 
  let rec aux = function
  | [] -> " "
  | aty :: aty_list ->
     print_aty aty ^ " ; " ^ aux aty_list
  in aux list

let string_from_label_list list = 
  let rec aux = function
  | [] -> " "
  | l :: ll ->
     (match l with LId(x) -> x) ^ " ; " ^ aux ll
  in aux list

(** [check_program_is_fully_annotated ast] performs a syntactic check
 that the programmer wrote sufficient type annotations for [typecheck]
 to run correctly. *)
let check_program_is_fully_annotated ast =
  (**
      We check the presence of type ascriptions on:
      - variables
      - tagged values patterns
   *)
  let rec program p = List.iter (located definition) p

  and definition _ = function
    | DefineValue vdef ->
      value_definition vdef
    | _ ->
      ()

  and value_definition = function
    (** A toplevel definition for a value. *)
    | SimpleValue (x, s, e) ->
       if s = None then missing_type_annotation (Position.position x);
       located expression e
    (** A toplevel definition for mutually recursive functions. *)
    | RecFunctions fs ->
       List.iter function_definition fs

  and function_definition = function
    | (f, s, FunctionDefinition (_, e)) ->
       if s = None then missing_type_annotation (Position.position f);
       located expression e

  and expression pos = function
    | Define (vdef, e) ->
       value_definition vdef;
       located expression e
    | Apply (a, b) ->
       List.iter (located expression) [a; b]
    | Tuple ts ->
       List.iter (located expression) ts
    | Record (fields, a) ->
       if a = None then type_error pos "A type annotation is missing.";
       List.iter (fun (_, e) -> located expression e) fields
    | TypeAnnotation ({ Position.value = Fun (FunctionDefinition (_, e)) },
                      _) ->
       located expression e
    | Fun (FunctionDefinition (_, _)) ->
       type_error pos "An anonymous function must be annotated."
    | Field (e, _) | TypeAnnotation (e, _) | Ref e | Read e ->
       located expression e
    | Sequence es ->
       List.iter (located expression) es
    | Tagged (_, a, es) ->
       if a = None then type_error pos "A type annotation is missing.";
       List.iter (located expression) es
    | For (_, e1, e2, e3) ->
       List.iter (located expression) (
           [ e1; e2; e3 ]
         )
    | IfThenElse (c, t, f) ->
       List.iter (located expression) [c; t; f]
    | Case (e, bs) ->
      located expression e;
      List.iter (located branch) bs
    | Assign (e1, e2) | While (e1, e2) ->
      located expression e1;
      located expression e2
    | Literal _ | Variable _ ->
      ()
  and pattern pos = function
    | PTypeAnnotation ({ Position.value = (PWildcard | PVariable _) }, _) ->
      ()
    | PRecord (fields, a) ->
       if a = None then type_error pos "A type annotation is missing.";
       List.iter (fun (_, p) -> located pattern p) fields
    | PTuple ps ->
       List.iter (located pattern) ps
    | PTypeAnnotation (p, _) ->
      located pattern p
    | PVariable _ | PWildcard ->
      missing_type_annotation pos
    | PTaggedValue (_, a, ps) ->
       if a = None then type_error pos "A type annotation is missing.";
       List.iter (located pattern) ps
    | POr ps | PAnd ps ->
      List.iter (located pattern) ps
    | PLiteral _ ->
      ()
  and branch _ = function
    | Branch (p, e) ->
      located pattern p;
      located expression e
  and missing_type_annotation pos =
    type_error pos "A type annotation is missing."
  in
  program ast

let invalid_instantiation pos given expected =
  type_error pos (
      Printf.sprintf
        "Invalid number of types in instantiation: \
         %d given while %d were expected." given expected
    )

(** [typecheck tenv ast] checks that [ast] is a well-formed program
    under the typing environment [tenv]. *)
let typecheck tenv ast : typing_environment =
  check_program_is_fully_annotated ast;

  let rec program p =
    List.fold_left (fun env x -> located (definition env) x) tenv p

  and definition tenv _ = function
    | DefineValue vdef ->
       value_definition tenv vdef

    | DefineType (t, ts, tdef) ->
       let ts = List.map Position.value ts in
       HopixTypes.bind_type_definition (Position.value t) ts tenv tdef

    | DeclareExtern (x, s) ->
       let s = located (type_scheme tenv) s in
       bind_value (Position.value x) s tenv

  and type_scheme tenv pos (ForallTy (ts, ty)) =
    let ts = List.map Position.value ts in
    let tenv = bind_type_variables pos tenv ts in
    Scheme (ts, internalize_ty tenv ty)

  and bind_type_variables pos tenv ts =
    List.iter (fun v ->
        if HopixTypes.is_type_variable_defined pos tenv v then
          type_error pos (
              Printf.sprintf
                "The type variable `%s' is already bound in the environment."
                (HopixPrettyPrinter.(to_string type_variable v))
            )
      ) ts;
    HopixTypes.bind_type_variables pos tenv ts

  and value_definition (tenv : typing_environment) = function
    | SimpleValue (x, Some s, e) ->
       let pos = Position.position s in
       let Scheme (ts, aty) as s = located (type_scheme tenv) s in
       let tenv' = bind_type_variables pos tenv ts in
       check_expression_monotype tenv' aty e;
       bind_value (Position.value x) s tenv

    | SimpleValue (_, _, _) ->
       assert false (* By check_program_is_fully_annotated. *)

    | RecFunctions fs ->
       recursive_definitions tenv fs

  and recursive_definitions tenv recdefs =
    let tenv =
      List.fold_left (fun tenv (f, fs, _) ->
          match fs with
          | None ->
             assert false  (* By check_program_is_fully_annotated. *)
          | Some fs ->
             let f = Position.value f in
             let fs = located (type_scheme tenv) fs in
             let fs = refresh_type_scheme fs in
             bind_value f fs tenv
        ) tenv recdefs
    in
    List.iter (fun (f, fs, d) ->
        match fs with
        | None ->
           assert false
        | Some fs ->
           let pos = Position.position f in
           let fs = located (type_scheme tenv) fs in
           check_function_definition pos tenv fs d
      ) recdefs;
    tenv

  (** [check_function_definition tenv fdef] checks that the
      function definition [fdef] is well-typed with respect to the
      type annotations written by the programmer. We assume that
      [tenv] already contains the type scheme of the function [f]
      defined by [fdef] as well as all the functions which are
      mutually recursively defined with [f]. *)
  and check_function_definition pos tenv aty = function
    | FunctionDefinition (p, e) ->
       match aty with
       | Scheme (ts, ATyArrow (_, out)) ->
          let tenv = bind_type_variables pos tenv ts in
          let tenv, _ = located (pattern tenv) p in
          check_expression_monotype tenv out e
       | _ ->
          type_error pos "A function must have an arrow type."

  (** [check_expected_type pos xty ity] verifies that the expected
      type [xty] is syntactically equal to the inferred type [ity]
      and raises an error otherwise. *)
  and check_expected_type pos xty ity =
    if xty <> ity then
      type_error pos (
          Printf.sprintf "Type error:\nExpected:\n  %s\nGiven:\n  %s\n"
            (print_aty xty) (print_aty ity)
        )

  and check_expected_label_type pos l xty ity = 
  if xty <> ity then
    type_error pos (
        Printf.sprintf "The field `%s` as type `%s' while it should have type `%s'."
        ( match Position.value l with LId(x) -> x )  (print_aty ity) (print_aty xty)
      )

  and check_expected_pattern_type pos xty ity = 
  if xty <> ity then
    type_error pos (
        Printf.sprintf "This pattern is not compatible with the matched value."
    )

  and check_same_pattern_type pos xty ity = 
  if xty <> ity then
    type_error pos (
        Printf.sprintf "All patterns must have the same type."
    )

  (** [check_expression_monotype tenv xty e] checks if [e] has
      the monotype [xty] under the context [tenv]. *)
  and check_expression_monotype tenv xty e : unit =
    let pos = Position.position e in
    let ity = located (type_of_expression tenv) e in
    check_expected_type pos xty ity

  and type_of_expression' tenv e = 
    located (type_of_expression tenv) e

  (** [type_of_expression tenv pos e] computes a type for [e] if it exists. *)
  and type_of_expression tenv pos : expression -> aty = function

    (*
       ——————————————    ———————————————    —————————————————
       Γ ⊢ n : int        Γ ⊢ c : char        Γ ⊢ s : string 
                                                                *)
    | Literal l -> type_of_literal (Position.value l)

    | Variable (x, types) -> 
      begin match types with 
        | Some types -> 
          let atySchemeFromX = located lookup_type_scheme_of_value x tenv in 
          let atyListFromTypes = List.map (fun ty -> internalize_ty tenv ty) types in
          let tau = try instantiate_type_scheme atySchemeFromX atyListFromTypes with 
          | InvalidInstantiation (s, s') -> invalid_instantiation pos s' s
          in tau
        | None -> 
        let look = try located lookup_type_scheme_of_value x tenv with
        | UnboundIdentifier (pos,Id(id)) -> type_error pos (
          Printf.sprintf "Unbound value `%s'." id
        )
        in type_of_monotype look
      end

    | TypeAnnotation (e, ty) ->
      let t = type_of_expression' tenv e in
      check_expected_type pos (internalize_ty tenv ty) t;
      t

    (*  
        Γ ⊢ e : σ
        ——————————————————
        Γ ⊢ ref e : ref(σ)  
                            *)
    | Ref e ->
        let t = type_of_expression' tenv e in
        href t

    (* 
        Γ ⊢ e : ref(σ)
        ——————————————
        Γ ⊢ !e : σ       
                        *)
    | Read e ->
        let oneRef = type_of_expression' tenv e in
        let type_ = try type_of_reference_type oneRef with 
        | NotAReference -> type_error pos "This isn't a reference."
        in type_ 
    
    (* 
        Γ ⊢ a -> τ1 : τ   Γ ⊢ b : τ1  
        ——————————————————————————————
                Γ ⊢ ab : τ      
                                        *)
    | Apply (a, b) ->
      let tyFromA = type_of_expression' tenv a in
      begin match tyFromA with 
      | ATyArrow (atyInput,atyOutput) ->
        check_expression_monotype tenv atyInput b;
        atyOutput  
      | _ -> type_error pos "Only functions can be applied."
      end


    (**         Another way 
    
    | Apply (a, b) ->
      let tyFromA = type_of_expression' tenv a in
      let outputTyOfA = try output_type_of_function tyFromA with 
      | NotAFunction -> type_error pos "Only functions can be applied."
      in 
      let inputTyOfA = input_type_of_function tyFromA in
      check_expression_monotype tenv inputTyOfA b;
      outputTyOfA
    *)   


    (* 
        Γ ⊢ e : ref(σ)    Γ ⊢ e' : τ
        ————————————————————————————
            Γ ⊢ e := e' : unit           
                                      *)
    | Assign (e, e') ->
      let oneRef = type_of_expression' tenv e in
      let t = type_of_reference_type oneRef in
      let tyToWrite = type_of_expression' tenv e' in
      check_expected_type (Position.position e') t tyToWrite;
      hunit

    (*
        Γ ⊢ e : bool    Γ ⊢ e' : unit
        —————————————————————————————
        Γ ⊢ while e { e' } : unit     
                                        *)
    | While (e, e') ->
      check_expression_monotype tenv hbool e;
      check_expression_monotype tenv hunit e';
      type_of_expression' tenv e'

    (*
        Γ ⊢ e1 : int    Γ ⊢ e2 : int    Γ ⊢ e : unit
        ——————————————————————————————————————————————
              Γ ⊢ for (id,e1,e2,e) : unit     
                                                        *)
    | For (id, e1, e2, e) -> 
      check_expression_monotype tenv hint e1;
      check_expression_monotype tenv hint e2; 
      let tenv = bind_value (Position.value id) (Scheme([],hint)) tenv in 
      check_expression_monotype tenv hunit e;
      hunit

    (*
        Γ ⊢ cnd : bool   Γ ⊢ e : unit    Γ ⊢ e' : unit
        ——————————————————————————————————————————————
              Γ ⊢ if cnd then e else e' : unit     
                                                        *)
    | IfThenElse (cnd, e, e') ->
      check_expression_monotype tenv hbool cnd;
      check_expression_monotype tenv hunit e;
      check_expression_monotype tenv hunit e';
      hunit

    (*
          Γ ⊢ e1;..;en-1 : hunit     Γ ⊢ en : σ
        ——————————————————————————————————————————
                   Γ ⊢ e1;e2;...;en : σ
                                                    *)
    | Sequence es ->
      let rec aux = function
      | [] -> assert false
      | [e] -> type_of_expression' tenv e 
      | e :: e_list ->
        check_expression_monotype tenv hunit e;
        aux e_list
      in aux es  
      
    (**         Another way 

    | Sequence es ->
      begin match List.rev es with
      | [] -> assert false
      | e :: es -> List.iter (fun e -> check_expression_monotype tenv hunit e) es;
      type_of_expression' tenv e
      end
    *)

    | Tuple es -> 
      let tys = List.map (fun e -> type_of_expression' tenv e) es in
      hprod tys

    | Define (vd, e) -> type_of_expression' (value_definition tenv vd) e  

    | Fun fdef ->
      begin match fdef with
      | FunctionDefinition (p, e) ->
        let penv, ts = located (pattern tenv) p in
        let t = type_of_expression' penv e in
        (*check_expected_type pos ts t;*)
        ATyArrow(ts, t)
      end

    | Field (_, l) ->
      let atySchemeFromL = 
        try lookup_type_scheme_of_label (Position.value l) tenv with
        | UnboundLabel -> type_error pos (
          Printf.sprintf "Unbound label `%s'." @@ match Position.value l with LId(x) -> x
          (* Or ---->  (HopixPrettyPrinter.(to_string label (Position.value l)))  *)
        )
      in
      (* Need to retrieve the value associated with the label ..*)
      begin match atySchemeFromL with 
      | Scheme (_,ATyArrow(_,out)) -> out
      | _ -> assert false
      end

    | Record (le, Some types) ->
      let rec aux rec_type = function
        | [] -> rec_type
        | (l,e) :: le' ->
          let atySchemeFromL = lookup_type_scheme_of_label (Position.value l) tenv in 
          let atyListFromTypes = List.map (fun ty -> internalize_ty tenv ty) types in
          let atyFromE = type_of_expression' tenv e in 
          let tau = try instantiate_type_scheme atySchemeFromL atyListFromTypes with
          | InvalidInstantiation (s, s') -> invalid_instantiation pos s' s
          in
          begin match tau with 
          | ATyArrow (ins, out) -> 
            check_expected_label_type pos l out atyFromE;
            aux ins le'
          | _ -> assert false
          end 
      in aux hunit le

    | Tagged (c, Some types, args) ->
      let atySchemeFromC = 
        try lookup_type_scheme_of_constructor (Position.value c) tenv with
        | UnboundConstructor -> type_error pos (
          Printf.sprintf "Unbound constructor `%s'." @@ match Position.value c with KId(x) -> x
          (* Or ----> (HopixPrettyPrinter.(to_string dataconstructor (Position.value c)))*)
        ) in 
      let atyListFromTypes = List.map (fun ty -> internalize_ty tenv ty) types in
      let tau = try instantiate_type_scheme atySchemeFromC atyListFromTypes with
      | InvalidInstantiation (s, s') -> invalid_instantiation pos s' s
      in 
      let aty_list, aty_out = destruct_arrows tau in 
        let check_arguments () =
          try
            List.iter2 (fun t e -> let atyFromE = type_of_expression' tenv e in
            check_expected_type (Position.position e) t atyFromE) aty_list args
          with
          | Invalid_argument _ -> type_error pos "Only functions can be applied." (* Should check if function? .*)
        in
        check_arguments ();
        aty_out
        

    | Case (e, bs) ->
      let atyFromE = type_of_expression' tenv e in
      branches tenv atyFromE bs

    | Tagged (_, None, _) | Record (_, None)  -> assert false (* By check_program_is_fully_annotated. *)

  and type_of_literal = function

    (*
        —————————
        ⊢ n : int 
                    *)    
    | LInt _ -> hint

    (*
        ————————————
        ⊢ s : string 
                      *)
    | LString _ -> hstring

    (*
        ————————————
        ⊢ c : char 
                      *)
    | LChar _ -> hchar

    
  (* [branches tenv t bs] check that the pattern introduced by all the branches [bs] have the same
     Type as the expression [e] from Case(e, b) and that all the expressions [e] from Branch(p, e) 
     have a common type. *)    
  and branches tenv t bs =
    let aty = List.fold_left (fun t2 b -> branch tenv t t2 (Position.value b)) None bs
    in
    match aty with
    | None-> assert false 
    | Some aty -> aty

  and branch tenv t t2 = function
    | Branch (p, e) ->
        let penv, pAty = located (pattern tenv) p in
        let tE = type_of_expression' penv e in
        (* Retrieving the [Branch] position by joining the positions start_position of the pattern [p] \ end_position of the expression [e]. *)
        let branch_pos = Position.(lex_join (start_of_position (position p)) (end_of_position (position e))) in
        (* Checking if the pattern type is the same as the expression e from [Case (e, b)] one. *)
        check_expected_pattern_type branch_pos pAty t;
        match t2 with
        | Some x ->
            check_expected_type (Position.position e) x tE;
            Some tE
        | None -> Some tE
    
  and patterns tenv = function
    | [] -> tenv, []
    | p :: ps ->
       let tenv, ty = located (pattern tenv) p in
       let tenv, tys = patterns tenv ps in
       tenv, ty :: tys

  and pattern tenv pos p =
    (* Checking that there is no duplicate of identifiers in the pattern [p].
       First by trying to retrieve the identifier in the (id) list 
       And then find it's position by assoc in the (id, pos) list, retrieved by variables_of_pattern. *)
    let p_identifiers = List.map (fun (id,_) ->  id) (variables_of_pattern p) in
    let id_pos_list = variables_of_pattern p in 
    try
      let id = List.find_duplicate p_identifiers in
        type_error (List.assoc id id_pos_list) "This is the second occurrence of this variable in the pattern."
    with
    | Not_found -> ();
    pattern' tenv pos p

  and check_all_patterns tenv pos ps =
  let prevenv, prevty = located (pattern' tenv) ((List.hd ps)) in
  let tenv = List.fold_left (
    fun env p ->
      let env, ty = located (pattern' env) p in
      check_same_pattern_type pos ty prevty ;
      env
  ) prevenv ps
  in tenv, prevty

  (** [pattern tenv pos p] computes a new environment completed with
      the variables introduced by the pattern [p] as well as the type
      of this pattern. *)
  and pattern' tenv pos = function

    (*
       ———————————————————————
       Γ ⊢ x : τ ⇑ Γ'(x : τ), τ 
                                  *)
    | PTypeAnnotation ({ Position.value = PVariable x }, ty) ->
        let aty = internalize_ty tenv ty in
        let penv = bind_value (Position.value x) (monotype aty) tenv in
        penv, aty

    (*
       ———————————————————————
       Γ ⊢ _ : τ ⇑ Γ , τ 
                                *)
    | PTypeAnnotation ({Position.value = PWildcard }, ty) ->
        (tenv, internalize_ty tenv ty)
    
    (*
       Γ ⊢ p ⇑ Γ', τ'     τ' = τ
       ————————————————————————————
       Γ ⊢ p : τ ⇑ Γ', τ        
                                    *)
    | PTypeAnnotation (p, ty) ->
        let penv, tau' = located (pattern tenv) p in
        let aty = internalize_ty tenv ty in
        check_expected_type pos aty tau';
        penv, aty

    (*
       ——————————————    ———————————————    —————————————————
       Γ ⊢ n ⇒ Γ, int    Γ ⊢ c ⇒ Γ, char    Γ ⊢ s ⇒ Γ, string 
                                                                *)
    | PLiteral l ->
      tenv, type_of_literal (Position.value l)    
      
    | PTuple ps -> 
        let penv, pt = patterns tenv ps in 
        penv, hprod pt

    | PTaggedValue(c, Some types, ps) ->
        let atySchemeFromC = try lookup_type_scheme_of_constructor (Position.value c) tenv with 
        | UnboundConstructor -> type_error pos (
          Printf.sprintf "Unbound constructor `%s'." @@ match Position.value c with KId(x) -> x 
        )
        in 
        let penv, p_list = patterns tenv ps in
        let atyListFromTypes = List.map (fun t -> internalize_ty tenv t) types in
        let tau = try instantiate_type_scheme atySchemeFromC atyListFromTypes with 
        | InvalidInstantiation (s, s') -> invalid_instantiation pos s' s
        in 
        let aty_list, aty_out = destruct_arrows tau in 
        let check_patterns () = 
          try List.iter2 (fun p t -> check_expected_type pos t p) p_list aty_list with 
          | Invalid_argument _ -> assert false
        in check_patterns ();
        penv, aty_out


    (*
        T(α):={l1 :τ1;...;ln :τn} ∈ Γ0 ∀ i ∈ [1..n], Γi−1 ⊢mi : τi [α􏰀→τ] ⇑ Γi 
      ———————————————————————————————————————————————————————————————————————————
            Γ0 ⊢ {l1 = m1;...;ln = mn}[τ] : T(τ) ⇑ Γn                             
                                                                                  *)
    | PRecord(lp ,Some types) ->
      let atys = List.map (fun t -> internalize_ty tenv t) types in
      let rec aux env record_type = function
        | [] -> env, record_type
        | (l,p) :: lp' ->
          let atySchemeFromL = try lookup_type_scheme_of_label (Position.value l) tenv with
            | UnboundLabel -> type_error pos (
              Printf.sprintf "There is no type definition for the label `%s'." @@ match Position.value l with LId(x) -> x
            )
          in 
          let penv, atyP = pattern env pos (Position.value p) in 
          let tau = try instantiate_type_scheme atySchemeFromL atys with
          | InvalidInstantiation (s, s') -> invalid_instantiation pos s' s
          in
          begin match tau with 
          | ATyArrow (ins, out) ->
            check_expected_label_type pos l out atyP;
            aux penv ins lp'
          | _ -> assert false
          end
      in 
      aux tenv hunit lp
    
    (* We check that all the patterns from the [ps] pattern list have the same type. *) 
    | POr ps | PAnd ps -> check_all_patterns tenv pos ps

    | PWildcard | PVariable _ | PTaggedValue (_, None, _) | PRecord (_, None ) -> 
      assert false (* By check_program_is_fully_annotated. *)

  in
  program ast

let print_typing_environment = HopixTypes.print_typing_environment

