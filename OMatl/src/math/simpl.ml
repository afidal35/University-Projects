open Syntax
open Utils
open Algebra

module Simpl : sig
  val reduce : expression -> expression
end = struct
  let raise_power_exp e f =
    let b = fst e in
    let e = snd e in
    (b, Fract.( * ) e f)

  let invert_exp e = raise_power_exp e (frac_of_poly (poly_of_float (-1.)))

  let negate_term = function
    | Poly p -> Leaf (Poly (Polynom.( ~- ) p))
    | Frac (n, d) -> Leaf (Frac (Fract.( ~- ) (n, d)))
    | Exponent (b, e) -> App2 (Mult, [Leaf (Exponent (b, e)); Light.m_one])
    | App0 op0 -> (
      match op0 with
      | Pi -> Light.float_leaf (Float.neg Float.pi)
      | E -> Light.float_leaf (Float.neg (exp 1.)) )

  let rec negate_expression = function
    | Leaf t -> negate_term t
    | App1 (c, o, e, ex) -> App1 (negate_expression c, o, e, ex)
    | App2 (o, el) -> App2 (Mult, [App2 (o, el); Light.m_one])

  let invert_term = function
    | Poly p -> Leaf (Frac (poly_of_float 1., p))
    | Frac (n, d) -> Leaf (Frac (Fract.( ~/ ) (n, d)))
    | Exponent (b, e) -> Leaf (Exponent (invert_exp (b, e)))
    | App0 op0 -> (
      match op0 with
      | Pi -> Leaf (Frac (poly_of_float 1., poly_of_float Float.pi))
      | E -> Leaf (Frac (poly_of_float 1., poly_of_float (Float.exp 1.))) )

  let invert_expression = function
    | Leaf t -> invert_term t
    | App1 (c, o, e, ex) -> App2 (Div, [Light.one; App1 (c, o, e, ex)])
    | App2 (o, el) -> App2 (Div, [Light.one; App2 (o, el)])

  let rec reduce e =
    match e with
    | Leaf t -> (
      match t with
      | Frac (n, d) -> reduce_frac n d
      | App0 op0 -> (
        match op0 with
        | Pi -> Light.float_leaf Float.pi
        | E -> Light.float_leaf (exp 1.) )
      | _ -> e )
    | App1 (_, UMinus, e, _) -> negate_expression e
    | App1 (c, op1, e, ex) -> Light.app1 op1 c ex (reduce e)
    | App2 (o, el) -> (
        let sel = List.map reduce el in
        let app2s = find_app2s sel in
        let terms = find_terms sel in
        let same_app2s = include_app2s_type app2s o in
        let diff_app2s = exclude_app2s_type app2s o in
        let a = List.fold_right ( @ ) (List.map snd app2s) [] in
        let app1s_child = find_app1s a in
        let app1s = app1s_child @ find_app1s sel in
        let child_exprs =
          List.fold_right ( @ ) (List.map snd same_app2s) []
        in
        let new_app2s =
          if o = Plus || o = Mult then diff_app2s @ find_app2s child_exprs
          else app2s
        in
        let new_terms =
          if o = Plus || o = Mult then terms @ find_terms child_exprs
          else terms
        in
        let poly_terms = include_terms_type new_terms 1 in
        let frac_terms = include_terms_type new_terms 2 in
        let exp_terms = include_terms_type new_terms 3 in
        match o with
        | Plus ->
            reduce_app2_plus app1s new_app2s poly_terms frac_terms exp_terms
        | Minus -> reduce_app2_minus sel
        | Mult ->
            reduce_app2_mult app1s new_app2s poly_terms frac_terms exp_terms
        | Div -> reduce_app2_div sel poly_terms
        | Expo -> reduce_app2_expo e sel poly_terms frac_terms )

  and reduce_frac n d =
    let p = Polynom.( * ) n (d |> List.hd |> Monom.( ~/ ) |> poly_of_mono) in
    let powerlists = List.fold_right ( @ ) (List.map snd p) [] in
    if List.length d = 1 && List.for_all (fun p -> snd p >= 0.) powerlists
    then Leaf (Poly p)
    else
      let gcd = Polynom.gcd n d in
      let newn = Polynom.( * ) n (gcd |> Monom.( ~/ ) |> poly_of_mono) in
      let newd = Polynom.( * ) d (gcd |> Monom.( ~/ ) |> poly_of_mono) in
      Leaf (Frac (newn, newd))

  and reduce_app2_plus app1s new_app2s poly_terms frac_terms exp_terms =
    let new_poly_frac_expr =
      if List.length frac_terms > 0 then
        [ reduce
            (Leaf
               (Frac
                  (List.fold_left Fract.( + )
                     (frac_of_poly (poly_of_float 0.))
                     (List.map frac_of_term (poly_terms @ frac_terms))))) ]
      else if List.length poly_terms > 0 then
        [ Leaf
            (Poly
               (List.fold_left Polynom.( + ) (poly_of_float 0.)
                  (List.map poly_of_term poly_terms))) ]
      else []
    in
    let simp_expr =
      if
        List.length new_poly_frac_expr = 1
        && new_poly_frac_expr |> List.hd |> term_of_expression
           |> is_term_float
        && new_poly_frac_expr |> List.hd |> term_of_expression
           |> float_of_term = 0.
      then []
      else new_poly_frac_expr
    in
    let repl_terms =
      sort compare_term (exp_terms @ List.map term_of_expression simp_expr)
    in
    let repl_exprs =
      sort compare_expr
        ( List.map expression_of_app2 new_app2s
        @ List.map expression_of_term repl_terms )
    in
    let same_app1s, diff_app1s =
      match app1s with
      | [] -> ([], [])
      | _ ->
          let _, op1, e, ex = List.hd app1s in
          ( include_app1s_type_sum app1s op1 e ex
          , exclude_app1s_type_sum app1s op1 e ex )
    in
    let app1s =
      if List.length same_app1s = 0 then diff_app1s
      else
        let new_app1s =
          if List.length same_app1s = 1 then List.hd same_app1s
          else
            List.fold_left Trigo.( + ) (List.hd same_app1s)
              (List.tl same_app1s)
        in
        [new_app1s] @ diff_app1s
    in
    let app1_exprs = List.map (fun a -> expression_of_app1 a) app1s in
    if List.length repl_exprs > 1 then App2 (Plus, repl_exprs @ app1_exprs)
    else if List.length repl_exprs = 1 && List.length app1_exprs = 0 then
      List.hd repl_exprs
    else if List.length repl_exprs = 1 && List.length app1_exprs > 0 then
      App2 (Plus, app1_exprs @ [List.hd repl_exprs])
    else if List.length app1s = 0 then Light.zero
    else App2 (Plus, app1_exprs)

  and reduce_app2_minus sel =
    reduce
      (App2
         (Plus, [List.hd sel; sel |> List.tl |> List.hd |> negate_expression]))

  and reduce_app2_mult app1s new_app2s poly_terms frac_terms exp_terms =
    let new_poly_frac_expr =
      if List.length frac_terms > 0 then
        [ reduce
            (Leaf
               (Frac
                  (List.fold_left Fract.( * )
                     (frac_of_poly (poly_of_float 1.))
                     (List.map frac_of_term (poly_terms @ frac_terms))))) ]
      else if List.length poly_terms > 0 then
        [ Leaf
            (Poly
               (List.fold_left Polynom.( * ) (poly_of_float 1.)
                  (List.map poly_of_term poly_terms))) ]
      else []
    in
    if
      List.length new_poly_frac_expr = 1
      && new_poly_frac_expr |> List.hd |> term_of_expression |> is_term_float
      && new_poly_frac_expr |> List.hd |> term_of_expression |> float_of_term
         = 0.
    then Light.zero
    else
      let simp_expr =
        if
          List.length new_poly_frac_expr = 1
          && new_poly_frac_expr |> List.hd |> term_of_expression
             |> is_term_float
          && new_poly_frac_expr |> List.hd |> term_of_expression
             |> float_of_term = 1.
        then []
        else new_poly_frac_expr
      in
      let repl_terms =
        sort compare_term (exp_terms @ List.map term_of_expression simp_expr)
      in
      let repl_exprs =
        sort compare_expr
          ( List.map expression_of_app2 new_app2s
          @ List.map expression_of_term repl_terms )
      in
      let same_app1s, diff_app1s =
        match app1s with
        | [] -> ([], [])
        | _ ->
            let _, op1, e, ex = List.hd app1s in
            ( include_app1s_type_sum app1s op1 e ex
            , exclude_app1s_type_sum app1s op1 e ex )
      in
      let app1s =
        if List.length same_app1s = 0 then diff_app1s
        else
          let new_app1s =
            if List.length same_app1s = 1 then List.hd same_app1s
            else
              List.fold_left Trigo.( * ) (List.hd same_app1s)
                (List.tl same_app1s)
          in
          [new_app1s] @ diff_app1s
      in
      let app1_exprs = List.map (fun a -> expression_of_app1 a) app1s in
      if List.length repl_exprs > 1 then App2 (Mult, repl_exprs @ app1_exprs)
      else if List.length repl_exprs = 1 && List.length app1_exprs = 0 then
        List.hd repl_exprs
      else if List.length repl_exprs = 1 && List.length app1_exprs > 0 then
        App2 (Mult, app1_exprs @ [List.hd repl_exprs])
      else if List.length app1s = 0 then Light.zero
      else App2 (Mult, app1_exprs)

  and reduce_app2_div sel poly_terms =
    let first = List.hd sel in
    let second = sel |> List.tl |> List.hd in
    if List.length poly_terms = 2 then
      reduce
        (Leaf
           (Frac
              ( first |> term_of_expression |> poly_of_term
              , second |> term_of_expression |> poly_of_term )))
    else if List.length (find_app1s sel) = 2 then App2 (Div, [first; second])
    else reduce (App2 (Mult, [first; invert_expression second]))

  and reduce_app2_expo e sel poly_terms frac_terms =
    let first = List.hd sel in
    let second = List.hd (List.tl sel) in
    let a = find_app1s sel in
    if List.length a > 0 then
      let f, s =
        match List.length a with
        | 2 -> (List.hd a, a |> List.tl |> List.hd |> expression_of_app1)
        | 1 -> (List.hd a, if is_app1 first then second else first)
        | _ -> failwith ""
      in
      let c, op1, e, ex = f in
      let exp =
        match (ex, s) with
        | Leaf (Poly p), Leaf (Poly p') ->
            Leaf (Poly (Polynom.( * ) p p')) |> reduce
        | Leaf (Frac f), Leaf (Frac f') ->
            Leaf (Frac (Fract.( * ) f f')) |> reduce
        | Leaf (Frac f), Leaf (Poly p) | Leaf (Poly p), Leaf (Frac f) ->
            Leaf (Frac (Fract.( * ) f (frac_of_poly p))) |> reduce
        | _ -> App2 (Mult, [ex; s]) |> reduce
      in
      App1 (c, op1, e, exp)
    else
      let second_float =
        is_term second && is_term_float (term_of_expression second)
      in
      if second_float && float_of_term (term_of_expression second) = 0. then
        Light.float_leaf 1.
      else if second_float && float_of_term (term_of_expression second) = 1.
      then first
      else if List.length poly_terms = 2 then
        let first_poly = poly_of_term (term_of_expression first) in
        let second_poly = poly_of_term (term_of_expression second) in
        if List.length first_poly = 1 && second_float then
          Leaf
            (Poly
               (poly_of_mono
                  (Monom.( ^ ) (List.hd first_poly)
                     (float_of_poly second_poly))))
        else
          Leaf (Exponent (frac_of_poly first_poly, frac_of_poly second_poly))
      else if List.length poly_terms + List.length frac_terms = 2 then
        let first_frac = frac_of_term (term_of_expression first) in
        let second_frac = frac_of_term (term_of_expression second) in
        Leaf (Exponent (first_frac, second_frac))
      else if
        is_term first && first |> term_of_expression |> get_term_type = 3
      then
        let exp = first |> term_of_expression |> exp_of_term in
        let base = fst exp in
        let exponent = snd exp in
        reduce
          (App2
             ( Expo
             , [Leaf (Frac base); App2 (Mult, [Leaf (Frac exponent); second])]
             ))
      else if is_app2 first && first |> app2_of_expression |> fst == Expo
      then
        let operands = app2_of_expression first |> snd in
        let base = List.hd operands in
        let exponent = operands |> List.tl |> List.hd in
        reduce (App2 (Expo, [base; App2 (Mult, [exponent; second])]))
      else e
end
