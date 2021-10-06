open Math
open Math.Eval
open Math.Subst
open Math.Syntax
open Math.PrettyPrinter

class virtual base_entry_box ~tooltips ~entry_plhs ~action_label ?packing
  ?show () =
  let v_box =
    GPack.vbox ~width:600 ~height:50 ~spacing:10 ~homogeneous:false ?packing
      ?show ()
  in
  object (base_entry)
    inherit GObj.widget v_box#as_widget

    val entries =
      List.map
        (fun plh ->
          GEdit.entry ~placeholder_text:plh ~max_length:60 ~width:350
            ~height:40 ~editable:true ~packing:v_box#pack ())
        entry_plhs

    val action_button =
      GButton.button ~label:action_label ~packing:v_box#pack ()

    val error_label = GMisc.label ~text:"" ~packing:v_box#pack ()

    initializer
    error_label#misc#modify_fg [(`NORMAL, `NAME "red")] ;
    action_button#misc#modify_bg [(`SELECTED, `BLACK)] ;
    List.iter2
      (fun entry tooltip -> entry#misc#set_tooltip_text tooltip)
      entries tooltips
    |> ignore

    method v_box = v_box

    method action_button = action_button

    method expr =
      List.map
        (fun entry ->
          Interpreter.parse_expression_from_string (entry#text ^ "\n"))
        entries

    method set = (List.nth entries 0)#set_text

    method insert s = base_entry#set ((List.nth entries 0)#text ^ s)

    method set_error = error_label#set_text

    method reset_error = base_entry#set_error ""

    method clear = base_entry#set ""

    method delete =
      match (List.nth entries 0)#text with
      | "" -> ()
      | t -> base_entry#set (String.sub t 0 (String.length t - 1))
  end

class eval_entry_box ?packing ?show () =
  let tooltips = ["Type your expression here"] in
  let entry_plhs = ["Expression"] in
  object
    inherit
      base_entry_box
        ~tooltips ~entry_plhs ~action_label:"Evaluate" ?packing ?show ()
  end

let eval_entry_box ?packing ?show () = new eval_entry_box ?packing ?show ()

class subst_entry_box ?packing ?show () =
  let tooltips =
    [ "Type your expression to substitute here"
    ; "Variable to substitute"
    ; "Replace it by" ]
  in
  let entry_plhs = ["To substitute"; "Variable"; "Replace by"] in
  object
    inherit
      base_entry_box
        ~tooltips ~entry_plhs ~action_label:"Substitute" ?packing ?show ()
  end

let subst_entry_box ?packing ?show () = new subst_entry_box ?packing ?show ()

class fn_entry_box ?packing ?show () =
  let tooltips = ["Type your function here"; "Variable to evaluate"] in
  let entry_plhs = ["f(x)"; "With respect to"] in
  object
    inherit
      base_entry_box
        ~tooltips ~entry_plhs ~action_label:"Analyze" ?packing ?show ()
  end

let fn_entry_box ?packing ?show () = new fn_entry_box ?packing ?show ()

class eq_entry_box ?packing ?show () =
  let tooltips = ["Equation"; "With respect to"] in
  let entry_plhs = ["Expression 1"; "(x)"] in
  object
    inherit
      base_entry_box
        ~tooltips ~entry_plhs ~action_label:"Solve" ?packing ?show ()
  end

let eq_entry_box ?packing ?show () = new eq_entry_box ?packing ?show ()

let commands =
  [ ( "Evaluation"
    , "eval(e)\n"
    , "Get the result of the expression [e] without variables.\n\
      \ Example : $ eval (3+3*4).\n" )
  ; ( "Substitution"
    , "subst(e, x, e')\n"
    , "Substitute the variable [x] from the expression [e] by the \
       expression [e'].\n\
      \ Example : $ subst (3+x, x, 100).\n" )
  ; ( "Simplification"
    , "simpl(e)\n"
    , "Simplifies the expression [e].\n Example : $ simpl (3+0).\n" )
  ; ( "Solve"
    , "solve(e, x)\n"
    , "Solves the equation represented by the expression [e] with respect \
       to [x].\n\
      \ Example : $ solve (x+3, x).\n" )
  ; ( "Derive"
    , "derive(e, x)\n"
    , "Calculates the differentiation of the expression [e] with respect to \
       [x].\n\
      \ Example : $ derive (x^2, x).\n" )
  ; ( "Integrate"
    , "integ(e, x, a, b)\n"
    , "Integrate the expression [e] with respect to [x] between [a] and [b].\n\
      \ Example : $ integ (x^3, x, 2, 4).\n" ) ]

class term_entry_box ?packing ?show () =
  object (term_entry)
    inherit
      base_entry_box
        ~tooltips:["Type your command"] ~entry_plhs:["Command"]
          ~action_label:"Process" ?packing ?show ()

    method cmd =
      let h = List.hd entries in
      Interpreter.parse_command_from_string (h#text ^ "\n")

    initializer
    List.iter
      (fun (lab, cmd, desc) ->
        let f =
          GBin.frame ~label:lab ~label_xalign:0.5 ~label_yalign:1.
            ~border_width:10 ~packing:term_entry#v_box#pack ()
        in
        let v_box =
          GPack.vbox ~width:400 ~height:50 ~spacing:10 ~homogeneous:false
            ~packing:f#add ()
        in
        GMisc.label ~text:cmd ~packing:v_box#pack () |> ignore ;
        GMisc.label ~text:desc ~packing:v_box#pack () |> ignore)
      commands
  end

let term_entry_box ?packing ?show () = new term_entry_box ?packing ?show ()

class virtual base_calc_box ?packing ?show () =
  let v_box =
    GPack.vbox ~width:600 ~height:400 ~spacing:20 ~homogeneous:false ?packing
      ?show ()
  in
  let v_box2 =
    GPack.vbox ~width:600 ~height:400 ~spacing:20 ~homogeneous:false ()
  in
  let expander =
    GBin.expander ~label:"Keypad" ~expanded:true ~packing:v_box2#pack ()
  in
  let h_tables_box =
    GPack.hbox ~spacing:10 ~homogeneous:false ~packing:expander#add ()
  in
  object (self)
    inherit GObj.widget v_box#as_widget

    val virtual entry_box : base_entry_box

    initializer
    GObj.pack_return entry_box#v_box ~packing:(Some v_box#pack) ~show
    |> ignore ;
    GObj.pack_return v_box2 ~packing:(Some v_box#pack) ~show |> ignore ;
    GCalcTable.calc_base_table ~command:self#command ~show:true
      ~packing:h_tables_box#pack ()
    |> ignore ;
    GCalcTable.calc_ext_table ~command:self#command
      ~packing:(h_tables_box#pack ~from:`END)
      ()
    |> ignore ;
    GCalcTable.calc_tools_table ~command:self#command ~packing:v_box2#pack ()
    |> ignore

    method entry_box = entry_box

    method v_box = v_box

    method command =
      function
      | "(x)" -> entry_box#insert "()"
      | "x^y" -> entry_box#insert "^"
      | "EXP" -> entry_box#insert ""
      | "AC" -> entry_box#clear
      | "DEL" -> entry_box#delete
      | s -> entry_box#insert s
  end

class eval_calc_box ?packing ?show () =
  object
    inherit base_calc_box ?packing ?show ()

    val entry_box = eval_entry_box ()
  end

let eval_calc_box ?packing ?show () = new eval_calc_box ?packing ?show ()

class subst_calc_box ?packing ?show () =
  object
    inherit base_calc_box ?packing ?show ()

    val entry_box = subst_entry_box ()
  end

let subst_calc_box ?packing ?show () = new subst_calc_box ?packing ?show ()

class eq_calc_box ?packing ?show () =
  object
    inherit base_calc_box ?packing ?show ()

    val entry_box = eq_entry_box ()
  end

let eq_calc_box ?packing ?show () = new eq_calc_box ?packing ?show ()

class fn_calc_box ?packing ?show () =
  object
    inherit base_calc_box ?packing ?show ()

    val entry_box = fn_entry_box ()
  end

let fn_calc_box ?packing ?show () = new fn_calc_box ?packing ?show ()

class virtual base_result_box ~expander_labels
  ~(header_labels : (string * string) list) ?packing ?show () =
  let v_box =
    GPack.vbox ~width:600 ~height:1500 ~spacing:100 ~homogeneous:false
      ?packing ?show ()
  in
  let expanders =
    List.map
      (fun l -> GBin.expander ~label:l ~packing:v_box#pack ())
      expander_labels
  in
  let frames =
    List.map2
      (fun ex (_, hdr) ->
        GBin.frame ~width:100 ~height:300 ~border_width:10 ~label:hdr
          ~label_xalign:0.5 ~label_yalign:1. ~packing:ex#add ())
      expanders header_labels
  in
  object (self)
    inherit GObj.widget v_box#as_widget

    initializer (List.nth expanders 0)#set_expanded true |> ignore

    val labels =
      List.map2
        (fun (n, _) f ->
          ( n
          , GMisc.label ~text:"" ~justify:`LEFT ~selectable:true
              ~packing:f#add () ))
        header_labels frames

    method get n = List.assoc n labels

    method set n s = (self#get n)#set_text s

    method v_box = v_box
  end

class eval_result_box ?packing ?show () =
  let expander_labels = ["Evaluation"; "More steps"] in
  let header_labels =
    [ ("eval", "This expression evaluates to : \n")
    ; ("steps", "We follow the PEMDAS order : \n") ]
  in
  object
    inherit base_result_box ~expander_labels ~header_labels ?packing ?show ()
  end

let eval_result_box ?packing ?show () = new eval_result_box ?packing ?show ()

class subst_result_box ?packing ?show () =
  let expander_labels = ["Substitution"; "Evaluation"; "Simplification"] in
  let header_labels =
    [ ("subst", "This substitution with respect to x is : \n")
    ; ("eval", "This expression evaluates to : \n")
    ; ("simpl", "This expression simplifies to : \n") ]
  in
  object
    inherit base_result_box ~expander_labels ~header_labels ?packing ?show ()
  end

let subst_result_box ?packing ?show () =
  new subst_result_box ?packing ?show ()

class eq_result_box ?packing ?show () =
  let expander_labels = ["Equation"] in
  let header_labels = [("eq", "This equation solves to : \n")] in
  object
    inherit base_result_box ~expander_labels ~header_labels ?packing ?show ()
  end

let eq_result_box ?packing ?show () = new eq_result_box ?packing ?show ()

class fn_result_box ?packing ?show () =
  let expander_labels =
    [ "Simplify"
    ; "Differentiate"
    ; "Antiderive"
    ; "Integrate"
    ; "Table of values" ]
  in
  let header_labels =
    [ ("simpl", "This expression simplifies to : \n")
    ; ("diff", "The derivative with respect to x is : \n")
    ; ("adiff", "The antiderivative with respect to x is : \n")
    ; ("integ", "Integrating with respect to x gives : \n")
    ; ("table", "Some sample points are : \n") ]
  in
  object
    inherit base_result_box ~expander_labels ~header_labels ?packing ?show ()
  end

let fn_result_box ?packing ?show () = new fn_result_box ?packing ?show ()

class term_result_box ?packing ?show () =
  let expander_labels = ["Result"] in
  let header_labels = [("res", "The result of this command gives : \n")] in
  object
    inherit base_result_box ~expander_labels ~header_labels ?packing ?show ()
  end

let term_result_box ?packing ?show () = new term_result_box ?packing ?show ()

class function_plot_box ?packing ?show () =
  let vbox =
    GPack.vbox ~width:200 ~height:600 ~spacing:20 ~homogeneous:false ?packing
      ?show ()
  in
  let fn_entry =
    GEdit.entry ~placeholder_text:"Function" ~packing:vbox#pack ()
  in
  let fn_variable =
    GEdit.entry ~placeholder_text:"Variable" ~packing:vbox#pack ()
  in
  let draw_button = GButton.button ~label:"Draw" ~packing:vbox#pack () in
  object (self)
    inherit GObj.widget vbox#as_widget

    method draw_button = draw_button

    method expr =
      List.map
        (fun entry ->
          Interpreter.parse_expression_from_string (entry#text ^ "\n"))
        [fn_entry; fn_variable]

    method evaluate i =
      Subst.substitute (List.nth self#expr 0)
        (List.nth self#expr 1 |> string_of_expr)
        (Light.float_leaf i)
      |> Eval.evaluate
  end

let function_plot_box ?packing ?show () =
  new function_plot_box ?packing ?show ()
