(* Syntaxe abstraite des expressions arithmétiques *)

type op0 = Pi | E

type op1 = Sqrt | Exp | Log | Sin | Cos | Tan | ASin | ACos | ATan | UMinus

type op2 = Plus | Mult | Minus | Div | Expo

type var = string

type num = float

type power = var * num

type monomial = num * power list

type polynomial = monomial list

type fraction = polynomial * polynomial

type exponent = fraction * fraction

type term =
  | App0 of op0
  | Poly of polynomial
  | Frac of fraction
  | Exponent of exponent

type expression =
  | Leaf of term
  | App1 of expression * op1 * expression * expression
  | App2 of op2 * expression list

type command =
  | Evaluate of expression
  | Substitute of expression * var * expression
  | Simplify of expression
  | Solve of expression * var
  | Derive of expression * var
  | Antiderive of expression * var
  | Integrate of expression * var * expression * expression
  | Plot of expression * var

module Light = struct
  let float_leaf f = Leaf (Poly [(f, [])])

  let m_one = float_leaf (-1.)

  let zero = float_leaf 0.

  let one = float_leaf 1.

  let two = float_leaf 2.

  let var_leaf v = Leaf (Poly [(1., [(v, 1.)])])

  let poly_leaf n x p = Leaf (Poly [(n, [(x, p)])])

  let pi = App0 Pi

  let e = App0 E

  let app1 op1 coeff exp e = App1 (coeff, op1, e, exp)

  let sqrt = app1 Sqrt one one

  let exp = app1 Exp one one

  let log = app1 Log one one

  let sin = app1 Sin one one

  let cos = app1 Cos one one

  let tan = app1 Tan one one

  let asin = app1 ASin one one

  let acos = app1 ACos one one

  let atan = app1 ATan one one

  let minest = app1 UMinus one one

  let app2 op2 e1 e2 = App2 (op2, [e1; e2])

  let ( + ) = app2 Plus

  let ( - ) = app2 Minus

  let ( * ) = app2 Mult

  let ( / ) = app2 Div

  let ( ^ ) = app2 Expo
end
