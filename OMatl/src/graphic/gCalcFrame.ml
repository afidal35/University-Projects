class virtual base_calc_frame ?packing ?show () =
  let frame =
    GBin.frame ~width:600 ~height:600 ~shadow_type:`NONE ?packing ?show ()
  in
  object
    inherit GObj.widget frame#as_widget

    val virtual calc_box : GCalcBox.base_calc_box

    initializer
    frame#set_halign `START ;
    GObj.pack_return calc_box#v_box ~packing:(Some frame#add) ~show |> ignore

    method frame = frame

    method expr = calc_box#entry_box#expr

    method set_error = calc_box#entry_box#set_error

    method reset_error = calc_box#entry_box#reset_error

    method get_calc_button = calc_box#entry_box#action_button
  end

class eval_calc_frame ?packing ?show () =
  object
    inherit base_calc_frame ?packing ?show ()

    val calc_box = GCalcBox.eval_calc_box ()
  end

let eval_calc_frame ?packing ?show () = new eval_calc_frame ?packing ?show ()

class subst_calc_frame ?packing ?show () =
  object
    inherit base_calc_frame ?packing ?show ()

    val calc_box = GCalcBox.subst_calc_box ()
  end

let subst_calc_frame ?packing ?show () =
  new subst_calc_frame ?packing ?show ()

class eq_calc_frame ?packing ?show () =
  object
    inherit base_calc_frame ?packing ?show ()

    val calc_box = GCalcBox.eq_calc_box ()
  end

let eq_calc_frame ?packing ?show () = new eq_calc_frame ?packing ?show ()

class fn_calc_frame ?packing ?show () =
  object
    inherit base_calc_frame ?packing ?show ()

    val calc_box = GCalcBox.fn_calc_box ()
  end

let fn_calc_frame ?packing ?show () = new fn_calc_frame ?packing ?show ()

class virtual base_result_frame ?packing ?show () =
  let scroll_win =
    GBin.scrolled_window ~width:600 ~height:600 ~hpolicy:`AUTOMATIC ?packing
      ?show ()
  in
  object
    inherit GObj.widget scroll_win#as_widget

    val virtual result_box : GCalcBox.base_result_box

    initializer
    scroll_win#misc#modify_bg [(`NORMAL, `BLACK)] ;
    scroll_win#misc#modify_fg [(`NORMAL, `WHITE)] ;
    GObj.pack_return result_box#v_box ~packing:(Some scroll_win#add) ~show
    |> ignore

    method result_box = result_box

    method set n s = result_box#set n s
  end

class eval_result_frame ?packing ?show () =
  object
    inherit base_result_frame ?packing ?show ()

    val result_box = GCalcBox.eval_result_box ()
  end

let eval_result_frame ?packing ?show () =
  new eval_result_frame ?packing ?show ()

class subst_result_frame ?packing ?show () =
  object
    inherit base_result_frame ?packing ?show ()

    val result_box = GCalcBox.subst_result_box ()
  end

let subst_result_frame ?packing ?show () =
  new subst_result_frame ?packing ?show ()

class eq_result_frame ?packing ?show () =
  object
    inherit base_result_frame ?packing ?show ()

    val result_box = GCalcBox.eq_result_box ()
  end

let eq_result_frame ?packing ?show () = new eq_result_frame ?packing ?show ()

class fn_result_frame ?packing ?show () =
  object
    inherit base_result_frame ?packing ?show ()

    val result_box = GCalcBox.fn_result_box ()
  end

let fn_result_frame ?packing ?show () = new fn_result_frame ?packing ?show ()
