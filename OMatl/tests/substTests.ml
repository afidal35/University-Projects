open Math
open Math.Subst
open Math.Simpl

let to_expr s = Parser.expression Lexer.token (Lexing.from_string (s ^ "\n"))

let%test "simpl1" = 
  Subst.substitute  (to_expr "x + x + 3") "x" (to_expr "20") 
  |> Simpl.reduce 
  |> PrettyPrinter.string_of_expr = "43"
  
let%test "simpl2" = 
  Subst.substitute  (to_expr "x + x + 2*x + x^2") "x" (to_expr "y") 
  |> Simpl.reduce 
  |> PrettyPrinter.string_of_expr = "y^2 + 4y"
