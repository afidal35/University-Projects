open Syntax

let string_of_op0 = function Pi -> "pi" | E -> "e"

let string_of_op1 = function
  | Sqrt -> "sqrt"
  | Exp -> "exp"
  | Log -> "log"
  | Sin -> "sin"
  | Cos -> "cos"
  | Tan -> "tan"
  | ASin -> "asin"
  | ACos -> "acos"
  | ATan -> "atan"
  | UMinus -> "-"

let string_of_op2 = function
  | Plus -> " + "
  | Mult -> " * "
  | Minus -> " - "
  | Div -> " / "
  | Expo -> " ^ "

let is_float_int f = f |> int_of_float |> float_of_int = f

let string_of_float_enh f =
  match is_float_int f with
  | true -> f |> int_of_float |> string_of_int
  | false -> string_of_float f

let string_of_power p =
  let v = fst p in
  let r = snd p in
  if r = 1. then v else v ^ "^" ^ string_of_float_enh r

let rec string_of_powerlist pl =
  match pl with
  | [] -> ""
  | x :: xs -> string_of_power x ^ string_of_powerlist xs

let string_of_mono m =
  let c = fst m in
  let pl = snd m in
  let absc = abs_float c in
  let pls = string_of_powerlist pl in
  (if c >= 0. then "" else "-")
  ^ (if absc = 1. && List.length pl > 0 then "" else string_of_float_enh absc)
  ^ pls

let string_of_poly p =
  let rec string_of_poly_helper lst =
    match lst with
    | [] -> ""
    | (c, pl) :: xs ->
        let absc = abs_float c in
        let pls = string_of_powerlist pl in
        (if c >= 0. then " + " else " - ")
        ^ ( if absc = 1. && List.length pl > 0 then ""
          else string_of_float_enh absc )
        ^ pls ^ string_of_poly_helper xs
  in
  match p with
  | [] -> ""
  | x :: xs -> string_of_mono x ^ string_of_poly_helper xs

let string_of_frac_multi_line f =
  let n = fst f in
  let d = snd f in
  let ns = string_of_poly n in
  let ds = string_of_poly d in
  if ds = "1" then
    (* Frac with denom of 1 gets converted to poly, but not if part of
       exponential *)
    ns
  else
    let rec string_of_dashes n =
      if n = 0 then "" else "-" ^ string_of_dashes (n - 1)
    in
    let m = max (String.length ns) (String.length ds) in
    ns ^ "\n" ^ string_of_dashes m ^ "\n" ^ ds

let string_of_frac f =
  let n = fst f in
  let d = snd f in
  let ns = string_of_poly n in
  let ds = string_of_poly d in
  if ds = "1" then ns else "(" ^ ns ^ ")/(" ^ ds ^ ")"

let string_of_exp e =
  let b = fst e in
  let e = snd e in
  "(" ^ string_of_frac b ^ ")^(" ^ string_of_frac e ^ ")"

let string_of_term = function
  | App0 op0 -> string_of_op0 op0
  | Poly p -> string_of_poly p
  | Frac (n, d) -> string_of_frac (n, d)
  | Exponent (b, e) -> string_of_exp (b, e)

let is_app1 = function App1 _ -> true | _ -> false

let rec string_of_expr = function
  | Leaf f -> string_of_term f
  | App1 (coeff, op1, e, exp) -> string_of_app1 coeff op1 e exp
  | App2 (op2, el) -> (
      let rec loop lst =
        match lst with
        | [] -> ""
        | x :: xs -> string_of_op2 op2 ^ string_of_expr x ^ loop xs
      in
      match el with
      | [] -> ""
      | x :: xs ->
          if is_app1 x then string_of_expr x ^ loop xs
          else "(" ^ string_of_expr x ^ loop xs ^ ")" )

and string_of_app1 coeff op1 e exp =
  let s = string_of_expr e in
  match op1 with
  | UMinus -> "-" ^ s
  | _ -> (
      ( match coeff with
      | Leaf (Poly [(1., [])]) -> ""
      | _ -> string_of_expr coeff )
      ^ string_of_op1 op1 ^ "(" ^ s ^ ")"
      ^
      match exp with
      | Leaf (Poly [(1., [])]) -> ""
      | _ -> "^" ^ string_of_expr exp )
