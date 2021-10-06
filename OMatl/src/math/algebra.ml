open Utils
open Syntax

module Monom = struct
  let simplify m =
    let c = fst m in
    match c with
    | 0. -> mono_of_float 0.
    | _ -> (c, List.filter (fun x -> snd x != 0.) (snd m))

  let mult m m' =
    simplify (fst m *. fst m', mult_powerlist_powerlist (snd m) (snd m'))

  let exp m n =
    (power (fst m) n, List.map (fun x -> raise_power_power x n) (snd m))

  let neg m = mult m (-1., [])

  let invert m = exp m (-1.)

  let gcd m m' =
    let f = fst m in
    let f' = fst m' in
    let gcd_coeff =
      if is_float_int f && is_float_int f' then
        gcd (int_of_float f) (int_of_float f') |> float_of_int
      else 1.
    in
    (gcd_coeff, gcd_powerlist (snd m) (snd m'))

  let ( * ) = mult

  let ( ^ ) = exp

  let ( ~- ) = neg

  let ( ~/ ) = invert
end

module Polynom = struct
  let simplify p =
    match List.length p with
    | 0 -> poly_of_float 0.
    | 1 -> p
    | _ ->
        let last = List.hd (List.rev p) in
        if fst last = 0. && last |> snd |> List.length = 0 then
          List.filter (fun x -> x |> snd |> List.length != 0) p
        else p

  let add_mono p m =
    let rec add_mono_helper p m so_far =
      match p with
      | [] -> m :: so_far
      | x :: xs ->
          if snd x = snd m then
            let c = fst x +. fst m in
            if c = 0. then so_far @ xs else ((c, snd x) :: so_far) @ xs
          else add_mono_helper xs m (x :: so_far)
    in
    add_mono_helper p m []

  let add p p' =
    let rec mult_helper p p' =
      match p' with [] -> p | x :: xs -> mult_helper (add_mono p x) xs
    in
    simplify (sort compare_mono (mult_helper p p'))

  let rec mult_mono p m =
    match p with [] -> [] | x :: xs -> Monom.( * ) x m :: mult_mono xs m

  let mult p p' =
    let rec mult_helper p p' =
      match p' with [] -> [] | x :: xs -> mult_mono p x @ mult_helper p xs
    in
    let mono_list = mult_helper p p' in
    List.fold_left add (poly_of_float 0.) (List.map poly_of_mono mono_list)

  let rec neg p =
    match p with [] -> [] | x :: xs -> Monom.( ~- ) x :: neg xs

  let gcd p p' =
    let ml = p @ p' in
    List.fold_left Monom.gcd (List.hd ml) (List.tl ml)

  let ( + ) = add

  let ( * ) = mult

  let ( ~- ) = neg
end

module Fract = struct
  let add f f' =
    let d1 = snd f in
    let d2 = snd f' in
    ( Polynom.( + ) (Polynom.( * ) (fst f) d2) (Polynom.( * ) (fst f') d1)
    , Polynom.( * ) d1 d2 )

  let mult f f' =
    (Polynom.( * ) (fst f) (fst f'), Polynom.( * ) (snd f) (snd f'))

  let neg f = (f |> fst |> Polynom.( ~- ), snd f)

  let invert f = (snd f, fst f)

  let ( + ) = add

  let ( * ) = mult

  let ( ~- ) = neg

  let ( ~/ ) = invert
end

module Trigo = struct
  let add t t' =
    let c, op, e, ex = t in
    let c', _, _, _ = t' in
    let new_coeff =
      match (c, c') with
      | Leaf (Poly p), Leaf (Poly p') -> Leaf (Poly (Polynom.( + ) p p'))
      | Leaf (Frac f), Leaf (Frac f') -> Leaf (Frac (Fract.( + ) f f'))
      | Leaf (Frac f), Leaf (Poly p) | Leaf (Poly p), Leaf (Frac f) ->
          Leaf (Frac (Fract.( + ) f (frac_of_poly p)))
      | _ -> App2 (Plus, [c; c'])
    in
    (new_coeff, op, e, ex)

  let mult t t' =
    let c, op, e, ex = t in
    let c', _, _, ex' = t' in
    let new_coeff =
      match (c, c') with
      | Leaf (Poly p), Leaf (Poly p') -> Leaf (Poly (Polynom.( * ) p p'))
      | Leaf (Frac f), Leaf (Frac f') -> Leaf (Frac (Fract.( * ) f f'))
      | Leaf (Frac f), Leaf (Poly p) | Leaf (Poly p), Leaf (Frac f) ->
          Leaf (Frac (Fract.( * ) f (frac_of_poly p)))
      | _ -> App2 (Mult, [c; c'])
    in
    let new_ex =
      match (ex, ex') with
      | Leaf (Poly p), Leaf (Poly p') -> Leaf (Poly (Polynom.( + ) p p'))
      | Leaf (Frac f), Leaf (Frac f') -> Leaf (Frac (Fract.( + ) f f'))
      | Leaf (Frac f), Leaf (Poly p) | Leaf (Poly p), Leaf (Frac f) ->
          Leaf (Frac (Fract.( + ) f (frac_of_poly p)))
      | _ -> App2 (Plus, [c; c'])
    in
    (new_coeff, op, e, new_ex)

  let ( + ) = add

  let ( * ) = mult
end
