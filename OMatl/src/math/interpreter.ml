open Syntax
open PrettyPrinter
open Eval
open Deriv
open Subst
open Simpl
open Integ
open Error

let parse_expression_from_string s =
  try Parser.expression Lexer.token (Lexing.from_string s)
  with _ -> unknown_expression ()

let parse_command_from_string s =
  try Parser.command Lexer.token (Lexing.from_string s)
  with _ -> unknown_command ()

let evaluate_command = function
  | Evaluate e -> (
    try Eval.steps_as_string e with Wrong_eval_type s -> s )
  | Substitute (e1, v, e2) -> (
    try Subst.substitute e1 v e2 |> Simpl.reduce |> string_of_expr
    with Variable_not_found s -> s )
  | Derive (e, v) -> (
    try Deriv.derive e v |> Simpl.reduce |> string_of_expr
    with
    | Unknown_derivative s
     |Variable_not_found s
     |Wrong_variable_multiplicity s
    ->
      s )
  | Antiderive (e, v) -> (
    try Integ.antiderive e v |> Simpl.reduce |> string_of_expr
    with
    | Unknown_antiderivative s
     |Variable_not_found s
     |Wrong_variable_multiplicity s
    ->
      s )
  | Integrate (e, v, a, b) -> (
    try Integ.integrate e v a b |> string_of_float_enh
    with
    | Unknown_antiderivative s
     |Variable_not_found s
     |Wrong_variable_multiplicity s
    ->
      s )
  | Simplify e -> Simpl.reduce e |> string_of_expr
  | Solve (e, v) -> Solver.resolve (Simpl.reduce e) v
  | _ -> failwith ""
