open Syntax
open Utils
open Error

module Subst : sig
  val substitute : expression -> string -> expression -> expression
end = struct
  let substitute_poly p v n =
    let m = List.hd p in
    let pl = snd m in
    let vars = List.map fst pl in
    let intvars = List.intersect vars [v] in
    if List.length intvars = 1 then
      let p = get_power pl v in
      App2 (Mult, [n; Leaf (Poly (p |> snd |> poly_of_float))])
    else Leaf (Poly p)

  let rec substitute' e v n =
    match e with
    | Leaf t -> substitute_poly (poly_of_term t) v n
    | App1 (_, op1, e', _) ->
        Light.app1 op1 Light.one Light.one (substitute' e' v n)
    | App2 (o, el) -> App2 (o, List.map (fun e -> substitute' e v n) el)

  let substitute e v n =
    if contains_variable v e then substitute' e v n
    else variable_not_found v e
end
