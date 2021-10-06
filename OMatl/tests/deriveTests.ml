open Math
open Math.Deriv
open Math.Simpl

let to_expr s = Parser.expression Lexer.token (Lexing.from_string (s ^ "\n"))

let%test "x^n" = 
  Deriv.derive (to_expr "x^3") "x" 
  |> Simpl.reduce 
  |> PrettyPrinter.string_of_expr 
  = "3x^2"

let%test "u+v" = 
  Deriv.derive (to_expr "x^3 + 2*x") "x" 
  |> Simpl.reduce 
  |> PrettyPrinter.string_of_expr 
  = "3x^2 + 2"

let%test "atan(u) + v" = 
  Deriv.derive (to_expr "atan(x^2) + 2*x^2") "x" 
  |> Simpl.reduce 
  |> PrettyPrinter.string_of_expr 
  = "(4x^5 + 6x)/(x^4 + 1)"
