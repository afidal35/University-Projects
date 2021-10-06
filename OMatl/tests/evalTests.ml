open Math
open Math.Eval 

let to_expr s = Parser.expression Lexer.token (Lexing.from_string (s ^ "\n"))

let%test "add" =  to_expr "3+3+3" |> Eval.evaluate = 9.
let%test "mult" =  to_expr "10*10*5" |> Eval.evaluate = 500.
let%test "sub" =  to_expr "3-3-3" |> Eval.evaluate = -3.
let%test "div" =  to_expr "10/5" |> Eval.evaluate = 2.
let%test "exp" =  to_expr "5^3" |> Eval.evaluate = 125.
let%test "mixup1" =  to_expr "2*3+2+(3-2)^3+3*11" |> Eval.evaluate = 42.
let%test "mixup2" =  to_expr "5^3-125+42" |> Eval.evaluate = 42.
let%test "mixup3" =  to_expr "42-42+pi-pi+42" |> Eval.evaluate = 42.
let%test "mixup4" =  to_expr "2^2^2*2+10" |> Eval.evaluate = 42.

