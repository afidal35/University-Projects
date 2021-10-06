class calc_notebook ?packing ?show () =
  let notebook = GPack.notebook ~tab_pos:`TOP ?packing ?show () in
  let label_eval = GMisc.label ~text:"Evaluation" () in
  let label_subst = GMisc.label ~text:"Substitution" () in
  let label_eq = GMisc.label ~text:"Equation" () in
  let label_fn = GMisc.label ~text:"Function" () in
  let label_plot = GMisc.label ~text:"Plot" () in
  let label_term = GMisc.label ~text:"Terminal" () in
  object
    inherit GObj.widget notebook#as_widget

    initializer
    GCalc.evaluation
      ~packing:(fun p ->
        notebook#append_page p ~tab_label:label_eval#coerce |> ignore)
      ()
    |> ignore ;
    GCalc.substitution
      ~packing:(fun p ->
        notebook#append_page p ~tab_label:label_subst#coerce |> ignore)
      ()
    |> ignore ;
    GCalc.equation
      ~packing:(fun p ->
        notebook#append_page p ~tab_label:label_eq#coerce |> ignore)
      ()
    |> ignore ;
    GCalc.fn
      ~packing:(fun p ->
        notebook#append_page p ~tab_label:label_fn#coerce |> ignore)
      ()
    |> ignore ;
    GCalcPlot.plot_frame
      ~packing:(fun p ->
        notebook#append_page p ~tab_label:label_plot#coerce |> ignore)
      ()
    |> ignore ;
    GCalc.terminal
      ~packing:(fun p ->
        notebook#append_page p ~tab_label:label_term#coerce |> ignore)
      ()
    |> ignore ;
    notebook#misc#modify_bg [(`NORMAL, `BLACK)] ;
    notebook#misc#modify_fg [(`NORMAL, `WHITE)]
  end

let calc_notebook ?packing ?show () = new calc_notebook ?packing ?show ()
