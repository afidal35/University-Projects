open Syntax
open Simpl
open PrettyPrinter

exception Variable_not_found of string

exception No_solution_found of string

let get_degree_equation e v =
  let aux e v =
    match e with
    | Leaf (Poly p) ->
        let first_monome = List.hd p in
        let varlist = snd first_monome in
        snd (List.find (fun var -> fst var = v) varlist)
    | _ -> failwith "only resolve polynome"
  in
  try aux e v
  with Not_found ->
    raise
    @@ Variable_not_found (v ^ " is not founded is the current expression")

let get_coeff_monome var degree e =
  let aux v degree e =
    match e with
    | Leaf (Poly p) ->
        fst
          (List.find
             (fun monome ->
               List.exists
                 (fun power -> fst power = v && snd power = degree)
                 (snd monome))
             p)
    | _ -> failwith "only resolve polynome"
  in
  try aux var degree e
  with Not_found ->
    failwith
      ( "degree " ^ string_of_float degree ^ " attach to " ^ var ^ " inside "
      ^ string_of_expr e ^ " is not found" )

(* b*x + c -> b,c *)
let get_coef_linear_equation e var =
  let coeff_b e = get_coeff_monome var 1. e in
  let coeff_c e =
    match e with
    | Leaf (Poly p) ->
        let last_monome = List.hd (List.rev p) in
        let coeff_c = fst last_monome in
        coeff_c
    | _ -> failwith "coef b do not match"
  in
  (coeff_b e, coeff_c e)

let solve_linear e var =
  let b = fst (get_coef_linear_equation e var) in
  let c = snd (get_coef_linear_equation e var) in
  [Leaf (Frac ([(-.c, [])], [(b, [])])) |> Simpl.reduce]

(* a*x^2 + b*x + c -> (a,(b,c)) *)
let get_coef_quadratic_equation e var =
  let a = get_coeff_monome var 2. e in
  (a, get_coef_linear_equation e var)

let solve_quadratic e var =
  let a, (b, c) = get_coef_quadratic_equation e var in
  let delta = (b ** 2.) -. (4. *. a *. c) in
  let unique_solution a b =
    [Leaf (Frac ([(-.b, [])], [(2. *. a, [])])) |> Simpl.reduce]
  in
  let multiple_solution a b delta =
    [ Leaf (Frac ([(-.b, []); (delta, [])], [(2. *. a, [])])) |> Simpl.reduce
    ; Leaf (Frac ([(-.b, []); (-.delta, [])], [(2. *. a, [])]))
      |> Simpl.reduce ]
  in
  if delta = 0. then unique_solution a b
  else if delta > 0. then multiple_solution a b delta
  else
    raise
    @@ No_solution_found
         ( "This expression "
         ^ PrettyPrinter.string_of_expr e
         ^ " has no real solution" )

let resolve e var =
  let solutions =
    match get_degree_equation e var with
    | 1. -> solve_linear e var
    | 2. -> solve_quadratic e var
    | _ -> failwith "This type of equation can not be solved yet"
  in
  String.concat " ; " (List.map string_of_expr solutions)
