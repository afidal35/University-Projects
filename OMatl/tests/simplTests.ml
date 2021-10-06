open Math
open Math.Simpl

let to_expr s = Parser.expression Lexer.token (Lexing.from_string (s ^ "\n"))

let%test "simpl1" = 
  to_expr "x + x + 2*x + x^2" 
  |> Simpl.reduce 
  |> PrettyPrinter.string_of_expr 
  = "x^2 + 4x"

let%test "simpl2" = 
  to_expr "(2*x^2 + 2*x)/2" 
  |> Simpl.reduce 
  |> PrettyPrinter.string_of_expr 
  = "x^2 + x"

let%test "simpl3" = 
  to_expr "cos(x) + cos(x) + cos(2*x)" 
  |> Simpl.reduce 
  |> PrettyPrinter.string_of_expr 
  = "2cos(x) + cos(2x)"


let%test "simpl4" = 
  to_expr "cos(x+x+x^2) + sin(2*x) + sin(2*x) + atan((2*x)/2)" 
  |> Simpl.reduce 
  |> PrettyPrinter.string_of_expr 
  = "cos(x^2 + 2x) + sin(2x) + sin(2x) + atan(x)"

