open Syntax
open Deriv
open Simpl
open Subst
open Eval
open Utils
open Error

module Integ : sig
  val antiderive : expression -> string -> expression

  val integrate : expression -> string -> expression -> expression -> float
end = struct
  let rec antiderive' e x =
    match e with
    (* k -> kx *)
    | Leaf (Poly [(n, [])]) -> Light.poly_leaf n x 1.
    | Leaf (App0 op0) ->
        let n = match op0 with Pi -> Float.pi | E -> exp 1. in
        Light.poly_leaf n x 1.
    (* x -> x^2/2 *)
    | Leaf (Poly [(1., [(x, 1.)])]) ->
        Light.( / ) (Light.poly_leaf 1. x 2.) Light.two
    | App1 _ -> antiderive_op1 x e
    | App2 _ -> antiderive_op2 x e
    | _ -> unknown_antiderivative ()

  and antiderive_op2 x = function
    (* 1/x^n -> 1/(-n+1)*x^(n-1) *)
    | App2
        ( Div
        , [ Leaf (Poly [(1., [])])
          ; App2
              (Expo, [Leaf (Poly [(1., [(x, 1.)])]); Leaf (Poly [(n, [])])])
          ] ) ->
        Light.( / ) Light.one
          (Light.poly_leaf (Float.neg n +. 1.) x (n -. 1.))
    (* x^n -> x^(n+1)/(n+1) *)
    | App2 (Expo, [Leaf (Poly [(1., [(x, 1.)])]); Leaf (Poly [(n, [])])]) ->
        Light.( / )
          (Light.poly_leaf 1. x (n +. 1.))
          (Light.float_leaf (n +. 1.))
    | App2
        ( Mult
        , [ Leaf (Poly [(n1, [])])
          ; App2
              (Expo, [Leaf (Poly [(1., [(x, 1.)])]); Leaf (Poly [(n2, [])])])
          ] ) ->
        let np1 = Light.float_leaf (n2 +. 1.) in
        let x_pow_np1 = Light.( ^ ) (Light.var_leaf x) np1 in
        Light.( * ) (Light.float_leaf n1) (Light.( / ) x_pow_np1 np1)
    (* u'u^n -> u^(n+1) / (n+1) *)
    | App2 (Mult, [u'; App2 (Expo, [u; Leaf (Poly [(n, [])])])])
      when Deriv.derive u x |> Simpl.reduce = (u' |> Simpl.reduce) ->
        let np1 = Light.float_leaf (n +. 1.) in
        Light.( / ) (Light.( ^ ) u np1) np1
    (* u'exp^u -> exp^u *)
    | App2 (Mult, [u'; App1 (_, Exp, u, _)])
      when Deriv.derive u x |> Simpl.reduce = (u' |> Simpl.reduce) ->
        Light.exp u
    (* u'/u^n -> - 1/(n-1)u^(n-1) *)
    | App2 (Div, [u'; App2 (Expo, [u; Leaf (Poly [(n, [])])])])
      when Deriv.derive u x |> Simpl.reduce = (u' |> Simpl.reduce) ->
        let nm1 = Light.float_leaf (n -. 1.) in
        let dividend = Light.( * ) nm1 (Light.( ^ ) u nm1) in
        Light.minest @@ Light.( / ) Light.one dividend
    (* u'/sqrt(u) -> 2sqrt(u) *)
    | App2 (Div, [u'; App1 (_, Sqrt, u, _)])
      when Deriv.derive u x |> Simpl.reduce = (u' |> Simpl.reduce) ->
        Light.( * ) Light.two (Light.sqrt u)
    (* u'/u -> ln u *)
    | App2 (Div, [u'; u])
      when Deriv.derive u x |> Simpl.reduce = (u' |> Simpl.reduce) ->
        Light.log u
    (* 1 + tan^2x = 1 / cos^2x -> tan(x) *)
    | App2
        ( Plus
        , [ Leaf (Poly [(1., [])])
          ; App2
              ( Expo
              , [ App1 (_, (Tan | Cos), Leaf (Poly [(1., [(x, 1.)])]), _)
                ; Leaf (Poly [(2., [])]) ] ) ] ) ->
        Light.tan (Light.var_leaf x)
    | _ -> unknown_antiderivative ()

  and antiderive_op1 _ = function
    | App1 (_, op1, Leaf (Poly [(1., [(x, 1.)])]), _) -> (
        let x = Light.var_leaf x in
        match op1 with
        | Sqrt ->
            let two_over_tree =
              Light.( / ) Light.two (Light.float_leaf 3.)
            in
            Light.( * ) two_over_tree
            @@ Light.( ^ ) x (Light.( / ) x two_over_tree)
        | Log -> Light.( - ) (Light.( * ) (Light.log x) x) x
        | Sin -> Light.minest @@ Light.cos x
        | Cos -> Light.sin x
        | Tan -> Light.minest @@ Light.log (Light.cos x)
        | ASin ->
            let x_by_asinx = Light.( * ) x (Light.asin x) in
            let mx_pow2_p1 =
              Light.( + ) (Light.minest @@ Light.( ^ ) x Light.two) Light.one
            in
            Light.( + ) x_by_asinx @@ Light.sqrt mx_pow2_p1
        | ACos ->
            let x_by_acosx = Light.( * ) x (Light.acos x) in
            let mx_pow2_m1 =
              Light.( + ) (Light.minest @@ Light.( ^ ) x Light.two) Light.one
            in
            Light.( - ) x_by_acosx @@ Light.sqrt mx_pow2_m1
        | ATan ->
            let x_by_atanx = Light.( * ) x (Light.atan x) in
            let x_pow2_p1 =
              Light.( + ) (Light.( ^ ) x Light.two) Light.one
            in
            let ln_xpow2p1_over_two =
              Light.( / ) (Light.log x_pow2_p1) Light.two
            in
            Light.( - ) x_by_atanx ln_xpow2p1_over_two
        | Exp -> Light.exp x
        | _ -> unknown_antiderivative () )
    | _ -> unknown_antiderivative ()

  let antiderive e x = check e x (antiderive' e x)

  let integrate e x a b =
    let antid = antiderive e x in
    let left = Subst.substitute antid x a |> Eval.evaluate in
    let right = Subst.substitute antid x b |> Eval.evaluate in
    right -. left
end
