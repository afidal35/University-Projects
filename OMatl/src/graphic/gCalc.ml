open Math
open Math.Solver
open Math.Error
open Math.Eval
open Math.Subst
open Math.Simpl
open Math.Deriv
open Math.Integ
open Math.PrettyPrinter

class virtual base_command =
  object (base)
    val virtual calc_frame : GCalcFrame.base_calc_frame

    val virtual result_frame : GCalcFrame.base_result_frame

    method expr = calc_frame#expr

    method virtual command : unit

    initializer
    calc_frame#get_calc_button#connect#clicked ~callback:(fun () ->
        base#command)
    |> ignore
  end

class evaluation ?packing ?show () =
  let h_paned =
    GPack.paned `HORIZONTAL ~width:1200 ~height:600 ?packing ?show ()
  in
  object (eval)
    inherit base_command

    inherit GObj.widget h_paned#as_widget

    val calc_frame = GCalcFrame.eval_calc_frame ~packing:h_paned#add1 ()

    val result_frame = GCalcFrame.eval_result_frame ~packing:h_paned#add2 ()

    method command =
      try
        let e = List.nth eval#expr 0 in
        e |> Eval.evaluate |> string_of_float |> result_frame#set "eval" ;
        e |> Eval.steps_as_string |> result_frame#set "steps" ;
        calc_frame#reset_error
      with Wrong_eval_type s | Unknown_expression s ->
        calc_frame#set_error s
  end

let evaluation ?packing ?show () = new evaluation ?packing ?show ()

class substitution ?packing ?show () =
  let h_paned =
    GPack.paned `HORIZONTAL ~width:1200 ~height:600 ?packing ?show ()
  in
  object (subst)
    inherit base_command

    inherit GObj.widget h_paned#as_widget

    val calc_frame = GCalcFrame.subst_calc_frame ~packing:h_paned#add1 ()

    val result_frame = GCalcFrame.subst_result_frame ~packing:h_paned#add2 ()

    method command =
      try
        let to_substitute = List.nth subst#expr 0 in
        let var = List.nth subst#expr 1 |> string_of_expr in
        let by = List.nth subst#expr 2 in
        let subst = Subst.substitute to_substitute var by in
        subst |> string_of_expr |> result_frame#set "subst" ;
        let evaluation =
          try subst |> Eval.evaluate |> string_of_float
          with Wrong_eval_type s -> s
        in
        result_frame#set "eval" evaluation ;
        let simplification =
          try subst |> Simpl.reduce |> string_of_expr
          with Wrong_eval_type s -> s
        in
        result_frame#set "simpl" simplification ;
        calc_frame#reset_error
      with
      | Variable_not_found s
       |Wrong_variable_multiplicity s
       |Unknown_expression s
      ->
        calc_frame#set_error s
  end

let substitution ?packing ?show () = new substitution ?packing ?show ()

class equation ?packing ?show () =
  let h_paned =
    GPack.paned `HORIZONTAL ~width:1200 ~height:600 ?packing ?show ()
  in
  object (eq)
    inherit base_command

    inherit GObj.widget h_paned#as_widget

    val calc_frame = GCalcFrame.eq_calc_frame ~packing:h_paned#add1 ()

    val result_frame = GCalcFrame.eq_result_frame ~packing:h_paned#add2 ()

    method command =
      try
        let e = List.nth eq#expr 0 in
        let v = List.nth eq#expr 1 |> string_of_expr in
        Solver.resolve (Simpl.reduce e) v |> result_frame#set "eq" ;
        calc_frame#reset_error
      with
      | Unknown_expression s | Variable_not_found s | No_solution_found s ->
        calc_frame#set_error s
  end

let equation ?packing ?show () = new equation ?packing ?show ()

class fn ?packing ?show () =
  let h_paned =
    GPack.paned `HORIZONTAL ~width:1200 ~height:600 ?packing ?show ()
  in
  object (fn)
    inherit base_command

    inherit GObj.widget h_paned#as_widget

    val calc_frame = GCalcFrame.fn_calc_frame ~packing:h_paned#add1 ()

    val result_frame = GCalcFrame.fn_result_frame ~packing:h_paned#add2 ()

    method command =
      try
        let e = List.nth fn#expr 0 in
        let v = List.nth fn#expr 1 |> string_of_expr in
        e |> Simpl.reduce |> string_of_expr |> result_frame#set "simpl" ;
        let derivative =
          try Deriv.derive e v |> Simpl.reduce |> string_of_expr
          with
          | Unknown_derivative s
           |Variable_not_found s
           |Wrong_variable_multiplicity s
          ->
            s
        in
        result_frame#set "diff" derivative ;
        let antiderivative =
          try Integ.antiderive e v |> Simpl.reduce |> string_of_expr
          with
          | Unknown_antiderivative s
           |Variable_not_found s
           |Wrong_variable_multiplicity s
          ->
            s
        in
        result_frame#set "adiff" antiderivative ;
        calc_frame#reset_error
      with Unknown_expression s -> calc_frame#set_error s
  end

let fn ?packing ?show () = new fn ?packing ?show ()

class terminal ?packing ?show () =
  let h_paned = GPack.paned `HORIZONTAL ~width:1200 ~height:600 () in
  let scroll_win1 =
    GBin.scrolled_window ~width:600 ~height:600 ~shadow_type:`NONE
      ~packing:h_paned#add1 ()
  in
  let scroll_win2 =
    GBin.scrolled_window ~width:600 ~height:600 ~hpolicy:`AUTOMATIC
      ~packing:h_paned#add2 ()
  in
  let calc_frame = GCalcBox.term_entry_box ~packing:scroll_win1#add () in
  let result_frame = GCalcBox.term_result_box ~packing:scroll_win2#add () in
  object (term)
    method command =
      let result =
        try calc_frame#cmd |> Interpreter.evaluate_command
        with Unknown_command c -> c
      in
      result_frame#set "res" result

    initializer
    GObj.pack_return h_paned ~packing ~show |> ignore ;
    calc_frame#action_button#connect#clicked ~callback:(fun () ->
        term#command)
    |> ignore
  end

let terminal ?packing ?show () = new terminal ?packing ?show ()
