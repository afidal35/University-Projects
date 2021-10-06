open Syntax
open PrettyPrinter
open Error

module List = struct
  include List

  let intersect l l' =
    List.(fold_left (fun acc x -> if mem x l then x :: acc else acc) [] l')

  let intersect_diff l l' =
    List.(fold_left (fun acc x -> if mem x l then acc else x :: acc) [] l')
end

let rec remove_dups lst =
  match lst with
  | [] -> []
  | h :: t -> h :: remove_dups (List.filter (fun x -> x <> h) t)

let variable_list_from =
  let rec aux list = function
    | Leaf (Poly [(_, [(v, _)])]) ->
        if List.mem v list then list else v :: list
    | App1 (_, _, e, _) -> aux list e
    | App2 (_, e) ->
        List.(flatten (map (fun exp -> aux list exp) e)) |> remove_dups
    | _ -> list
  in
  aux []

let contains_variable v e = List.mem v (variable_list_from e)

let contains_multiple_variables e = List.length (variable_list_from e) > 1

let check e x f =
  match contains_variable x e with
  | true -> (
    match contains_multiple_variables e with
    | true -> multiplicity_error e
    | false -> f )
  | false -> variable_not_found x e

let is_float_int f = float_of_int (int_of_float f) = f

let is_even i =
  let q = i / 2 in
  let r = i - (q * 2) in
  r = 0

let power a b =
  if a > 0. then exp (b *. log a)
  else if a = 0. then 0.
  else if is_float_int b then
    let i = int_of_float b in
    let p = exp (b *. log (-.a)) in
    if is_even i then p else -.p
  else
    failwith
      ( "Cannot raise negative number " ^ string_of_float a
      ^ " to non-integer power " ^ string_of_float b )

let rec gcd a b =
  let q = a / b in
  let r = a - (b * q) in
  if r = 0 then abs b else gcd b r

let get_expr_type = function Leaf _ -> 0 | App1 _ -> 1 | App2 _ -> 2

let get_term_type = function
  | Poly _ -> 1
  | Frac _ -> 2
  | Exponent _ -> 3
  | _ -> failwith "App0"

let is_term = function Leaf _ -> true | _ -> false

let rec get_power pl v =
  match pl with
  | [] ->
      failwith
        ( "Cannot find variable " ^ v ^ " in powerlist "
        ^ "string_of_powerlist pl" )
  | x :: xs -> if fst x = v then x else get_power xs v

let poly_of_term = function Poly p -> p | _ -> failwith ""

let term_of_expression = function Leaf t -> t | _ -> failwith ""

let is_app1 = function App1 _ -> true | _ -> false

let is_app2 = function App2 _ -> true | _ -> false

let app2_of_expression = function
  | App2 (o, el) -> (o, el)
  | _ -> failwith ""

let app1_of_expression = function
  | App1 (coeff, o, e, exp) -> (coeff, o, e, exp)
  | _ -> failwith ""

let find_terms el =
  List.fold_right
    (fun x acc -> if is_term x then term_of_expression x :: acc else acc)
    el []

let find_app2s el =
  List.fold_right
    (fun x acc -> if is_app2 x then app2_of_expression x :: acc else acc)
    el []

let find_app1s el =
  List.fold_right
    (fun x acc -> if is_app1 x then app1_of_expression x :: acc else acc)
    el []

let include_terms_type tl t = List.filter (fun x -> get_term_type x = t) tl

let exclude_terms_type tl t = List.filter (fun x -> get_term_type x != t) tl

let include_app2s_type nl op2 = List.filter (fun x -> fst x = op2) nl

let exclude_app2s_type nl op2 = List.filter (fun x -> fst x != op2) nl

let include_app1s_type_sum nl op1 e ex =
  List.filter (fun (_, op1', e', ex') -> op1' = op1 && e' = e && ex = ex') nl

let exclude_app1s_type_sum nl op1 e ex =
  List.filter
    (fun (_, op1', e', ex') -> op1' != op1 || e' <> e || ex <> ex')
    nl

let include_app1s_type_mult nl op1 e =
  List.filter (fun (_, op1', e', _) -> op1' = op1 && e' = e) nl

let exclude_app1s_type_mult nl op1 e =
  List.filter (fun (_, op1', e', _) -> op1' != op1 || e' <> e) nl

let expression_of_term t = Leaf t

let expression_of_app1 = function c, op1, e, ex -> App1 (c, op1, e, ex)

let expression_of_app2 n = App2 (fst n, snd n)

let compare_power a b =
  let avar = fst a in
  let bvar = fst b in
  if avar < bvar then true else if avar > bvar then false else snd a >= snd b

let compare_powerlist al bl =
  let avars = List.fold_left ( ^ ) "" (List.map fst al) in
  let bvars = List.fold_left ( ^ ) "" (List.map fst bl) in
  if avars = "" then false
  else if bvars = "" then true
  else if avars < bvars then true
  else if avars > bvars then false
  else
    let rec compare_raisedto_loop al bl =
      match al with
      | [] -> true
      | x :: xs -> (
        match bl with
        | [] -> false
        | y :: ys ->
            let araisedto = snd x in
            let braisedto = snd y in
            if araisedto > braisedto then true
            else if araisedto < braisedto then false
            else compare_raisedto_loop xs ys )
    in
    compare_raisedto_loop al bl

let compare_mono a b = compare_powerlist (snd a) (snd b)

let compare_term a b = get_term_type a >= get_term_type b

let compare_expr a b =
  let at = get_expr_type a in
  let bt = get_expr_type b in
  if at > bt then true
  else if at < bt then false
  else if is_term a then
    compare_term (term_of_expression a) (term_of_expression b)
  else true

let find_leftmost compare lst =
  let rec leftmost_helper compare so_far lst =
    match lst with
    | [] -> so_far
    | x :: xs ->
        leftmost_helper compare (if compare so_far x then so_far else x) xs
  in
  let rec leftmost_remove leftmost so_far lst =
    match lst with
    | [] -> None
    | x :: xs ->
        if x = leftmost then Some (x, so_far @ xs)
        else leftmost_remove leftmost (so_far @ [x]) xs
  in
  match lst with
  | [] -> None
  | x :: xs -> leftmost_remove (leftmost_helper compare x xs) [] lst

let rec sort compare lst =
  match find_leftmost compare lst with
  | None -> []
  | Some (x, xs) -> x :: sort compare xs

let mult_powerlist_power pl p =
  let rec mult_powerlist_power_helper pl p so_far =
    match pl with
    | [] -> so_far @ [p]
    | x :: xs ->
        if fst x = fst p then so_far @ [(fst x, snd x +. snd p)] @ xs
        else mult_powerlist_power_helper xs p (so_far @ [x])
  in
  mult_powerlist_power_helper pl p []

let mult_powerlist_powerlist pl1 pl2 =
  let rec mult_powerlist_powerlist_helper pl1 pl2 =
    match pl2 with
    | [] -> pl1
    | x :: xs ->
        mult_powerlist_powerlist_helper (mult_powerlist_power pl1 x) xs
  in
  sort compare_power (mult_powerlist_powerlist_helper pl1 pl2)

let gcd_powerlist pl1 pl2 =
  let rec gcd_powerlist_helper pl1 pl2 intvars so_far =
    match intvars with
    | [] -> so_far
    | x :: xs ->
        let p1 = snd (get_power pl1 x) in
        let p2 = snd (get_power pl2 x) in
        if is_float_int p1 && is_float_int p2 then
          gcd_powerlist_helper pl1 pl2 xs (so_far @ [(x, min p1 p2)])
        else gcd_powerlist_helper pl1 pl2 xs so_far
  in
  gcd_powerlist_helper pl1 pl2
    List.(intersect (map fst pl1) (map fst pl2))
    []

let mono_of_float f = (f, [])

let mono_of_var v = (1., [(v, 1.)])

let poly_of_mono m = [m]

let poly_of_float f = f |> mono_of_float |> poly_of_mono

let poly_of_var v = v |> mono_of_var |> poly_of_mono

let is_mono_float m = List.length (snd m) = 0

let float_of_mono m =
  if is_mono_float m then fst m
  else failwith ("Cannot convert monomial " ^ string_of_mono m ^ " to float")

let raise_power_power p n = (fst p, snd p *. n)

let is_term_poly = function Poly _ -> true | _ -> false

let is_poly_float p = List.length p = 1 && is_mono_float (List.hd p)

let is_term_float t = is_term_poly t && is_poly_float (poly_of_term t)

let float_of_poly p =
  if is_poly_float p then p |> List.hd |> float_of_mono
  else
    failwith ("Cannot convert polynomial " ^ string_of_poly p ^ " to float")

let float_of_term t =
  if is_term_float t then t |> poly_of_term |> float_of_poly
  else failwith ("Cannot convert term " ^ string_of_term t ^ "to float")

let frac_of_poly p = (p, poly_of_float 1.)

let frac_of_term t =
  match t with
  | Poly p -> frac_of_poly p
  | Frac (n, d) -> (n, d)
  | _ -> failwith ("Cannot convert term " ^ string_of_term t ^ " to fraction")

let exp_of_frac f = (f, frac_of_poly (poly_of_float 1.))

let exp_of_term = function
  | Poly p -> exp_of_frac (frac_of_poly p)
  | Frac (n, d) -> exp_of_frac (n, d)
  | Exponent (b, e) -> (b, e)
  | _ -> failwith ""
