open Syntax
open Error
open PrettyPrinter

module Eval : sig
  val evaluate : expression -> float

  val evaluate_by : op2 -> expression -> expression * expression list

  val steps_as_string : expression -> string
end = struct
  let ( ~> ) op1 e =
    match op1 with
    | Sqrt -> sqrt e
    | Log -> log e
    | Sin -> sin e
    | Cos -> cos e
    | Tan -> tan e
    | ASin -> asin e
    | ACos -> acos e
    | ATan -> atan e
    | UMinus -> ~-.e
    | Exp -> exp e

  (* Apply the operator extracted from the [op1] token to the expressions
     [e1] [e2]. *)
  let ( ~~> ) = function
    | Plus -> ( +. )
    | Minus -> ( -. )
    | Div -> ( /. )
    | Mult -> ( *. )
    | Expo -> ( ** )

  let rec evaluate = function
    | Leaf (Poly [(f, [])]) -> f
    | Leaf (App0 op0) -> ( match op0 with Pi -> Float.pi | E -> exp 1. )
    | App1 (_, op1, e, _) -> ~>op1 @@ evaluate e
    | App2 (op2, [e1; e2]) -> ( ~~> ) op2 (evaluate e1) (evaluate e2)
    | _ -> wrong_type ()

  (* Make a function to evaluate op1's *)
  let evaluate_by op2 e =
    let rec aux op2 e steps =
      let e' = eval op2 e in
      match e' with
      | e' when e' = e -> (e', steps)
      | _ -> aux op2 e' (e' :: steps)
    and eval op2 = function
      | Leaf (Poly [(f, [])]) -> Light.float_leaf f
      | Leaf (App0 op0) -> (
        match op0 with
        | Pi -> Light.float_leaf Float.pi
        | E -> Float.exp 1. |> Light.float_leaf )
      | App1 (_, op1, e, _) ->
          Light.float_leaf (~>op1 (eval op2 e |> evaluate))
      | App2 (op2', [Leaf (Poly [(f1, [])]); Leaf (Poly [(f2, [])])]) as e ->
          if op2' = op2 then Light.float_leaf (~~>op2' f1 f2) else e
      | App2 (opp2, [e1; e2]) -> Light.app2 opp2 (eval op2 e1) (eval op2 e2)
      | _ -> wrong_type ()
    in
    aux op2 e []

  let steps_as_string e =
    List.(
      fold_left
        (fun (e, acc) op2 ->
          let e', steps = evaluate_by op2 e in
          let all =
            match steps with
            | [] -> []
            | _ ->
                let steps' =
                  map
                    (fun step ->
                      Printf.sprintf "\n==> = %s \n" @@ string_of_expr step)
                    steps
                in
                let hdr =
                  Printf.sprintf
                    "\n--- Evaluating (%s) from expression --- : %s\n"
                    (string_of_op2 op2) (string_of_expr e)
                in
                steps' @ [hdr]
          in
          (e', all @ acc))
        (e, [])
        [Expo; Mult; Div; Plus; Minus]
      |> snd |> rev |> String.concat "")
end
