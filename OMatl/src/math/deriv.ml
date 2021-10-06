open Syntax
open Error
open Utils

module Deriv : sig
  val derive : expression -> string -> expression
end = struct
  let rec derive' e x =
    match e with
    (* k -> 0 *)
    | Leaf (Poly [(_, [])]) | Leaf (App0 _) -> Light.zero
    (* x -> 1 *)
    | Leaf (Poly [(1., [(_, 1.)])]) -> Light.one
    | App1 _ -> derive'_op1 x e
    | App2 _ -> derive'_op2 x e
    | _ -> unknown_derivative ()

  and derive'_op2 x = function
    (* u^n with n > 0 -> nx^(n-1) *)
    | App2 (Expo, [u; Leaf (Poly [(n, [])])]) when n > 0. ->
        let u_n_1 = Light.( ^ ) u (Light.float_leaf (n -. 1.)) in
        let n = Light.float_leaf n in
        let u' = derive' u x in
        Light.( * ) (Light.( * ) n u') u_n_1
    | App2 (op2, [u; v]) -> (
        let u' = derive' u x in
        let v' = derive' v x in
        match op2 with
        (* (u + v)' -> u' + v' *)
        | Plus -> Light.( + ) u' v'
        (* (u - v)' -> u' + (-v)' *)
        | Minus ->
            let _v' = derive' (Light.minest v) x in
            Light.( + ) u' _v'
        (* (uv)' -> u'v + uv' *)
        | Mult ->
            let u'v = Light.( * ) u' v in
            let uv' = Light.( * ) u v' in
            Light.( + ) u'v uv'
        (* (u/v)' -> (u'v - uv')/v^2 *)
        | Div ->
            let u'v = Light.( * ) u' v in
            let uv' = Light.( * ) u v' in
            let u'v_uv' = Light.( - ) u'v uv' in
            let v2 = Light.( ^ ) v Light.two in
            Light.( / ) u'v_uv' v2
        (* (a^u)' -> u'*(a^u)*ln(a) *)
        | Expo ->
            let ln_a = Light.log u in
            Light.( * ) (Light.( * ) u' @@ Light.( ^ ) u v) ln_a )
    | _ -> unknown_derivative ()

  and derive'_op1 x = function
    | App1 (_, op1, u, _) -> (
        let u' = derive' u x in
        match op1 with
        (* (-u)' -> -u' *)
        | UMinus ->
            let u' = derive' u x in
            Light.minest u'
        (* (cos u)' -> -u'(sin u) *)
        | Cos ->
            let min_u' = Light.minest u' in
            let sin_u = Light.sin u in
            Light.( * ) min_u' sin_u
        (* (sin u)' -> u'(cos u) *)
        | Sin ->
            let cos_u = Light.cos u in
            Light.( * ) u' cos_u
        (* (exp u)' -> u'(exp u) *)
        | Exp ->
            let exp_u = Light.exp u in
            Light.( * ) u' exp_u
        (* (ln u)' -> u'/u *)
        | Log -> Light.( / ) u' u
        (* (sqrt u)' -> u'/(2*(sqrt u)) *)
        | Sqrt ->
            let sqrt_u = Light.sqrt u in
            Light.( / ) u' @@ Light.( * ) Light.two sqrt_u
        (* (tan u)' -> u'/((cos u)^2) *)
        | Tan ->
            let cos_u = Light.cos u in
            Light.( / ) u' @@ Light.( ^ ) cos_u Light.two
        (* (acos u)' -> -u'/(sqrt(1-u^2)) *)
        | ACos ->
            let min_u' = Light.minest u' in
            let u_pow_2 = Light.( ^ ) u Light.two in
            let divisor = Light.sqrt @@ Light.( - ) Light.one u_pow_2 in
            Light.( / ) min_u' divisor
        (* (asin u)' -> u'/(sqrt(1-u^2)) *)
        | ASin ->
            let u' = Light.minest u' in
            let u_pow_2 = Light.( ^ ) u Light.two in
            let divisor = Light.sqrt @@ Light.( - ) Light.one u_pow_2 in
            Light.( / ) u' divisor
        (* (atan u)' -> u'/(1+u^2) *)
        | ATan ->
            let u_pow_2 = Light.( ^ ) u Light.two in
            let divisor = Light.( + ) Light.one u_pow_2 in
            Light.( / ) u' divisor )
    | _ -> unknown_derivative ()

  let derive e x = check e x (derive' e x)
end
