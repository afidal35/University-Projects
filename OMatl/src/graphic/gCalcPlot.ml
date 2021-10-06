open Cairo

class plot_frame ?packing ?show () =
  let plot_vbox =
    GPack.hbox ~width:1200 ~height:600 ~homogeneous:false ?packing ?show ()
  in
  let notebook_vbox =
    GPack.vbox ~width:200 ~height:600 ~homogeneous:false
      ~packing:plot_vbox#add ()
  in
  let table =
    GPack.table ~rows:1 ~columns:3 ~packing:notebook_vbox#pack ()
  in
  let notebook =
    GPack.notebook ~tab_pos:`TOP
      ~packing:(table#attach ~left:0 ~right:6 ~top:0)
      ()
  in
  let label_fn = GMisc.label ~text:"Function" () in
  let label_diff = GMisc.label ~text:"Derivative" () in
  let label_integ = GMisc.label ~text:"Integral" () in
  let function_box =
    GCalcBox.function_plot_box
      ~packing:(fun p ->
        notebook#append_page p ~tab_label:label_fn#coerce |> ignore)
      ()
  in
  let _ =
    GPack.vbox ~width:200 ~height:600 ~homogeneous:false
      ~packing:(fun p ->
        notebook#append_page p ~tab_label:label_diff#coerce |> ignore)
      ()
  in
  let _ =
    GPack.vbox ~width:200 ~height:600 ~homogeneous:false
      ~packing:(fun p ->
        notebook#append_page p ~tab_label:label_integ#coerce |> ignore)
      ()
  in
  let draw_vbox =
    GPack.vbox ~width:1000 ~height:600 ~homogeneous:false
      ~packing:plot_vbox#add ()
  in
  let draw_area = GMisc.drawing_area ~packing:draw_vbox#add () in
  object (self)
    inherit GObj.widget plot_vbox#as_widget

    method plot_vbox = plot_vbox

    method expr = function_box#expr

    method evaluate = function_box#evaluate

    method init_graph _ cr =
      (* Set source color for background drawing area [BLACK] and fill it. *)
      set_source_rgb cr 1. 1. 1. ;
      paint cr ;
      (* Translate the origin to the center of the box. *)
      translate cr (1000. /. 2.) (600. /. 2.) ;
      scale cr 80.0 (-60.0) ;
      (* Get the rectangle area. *)
      let _, _ = device_to_user_distance cr 1. 1. in
      let rectangle = clip_extents cr in
      (* Set the source color for drawing axes [WHITE]. *)
      set_line_width cr 0.03 ;
      set_source_rgb cr 0. 0. 0. ;
      (* Draw the [x] and [y] axes. *)
      move_to cr rectangle.x 0. ;
      line_to cr rectangle.w 0. ;
      move_to cr 0. rectangle.y ;
      line_to cr 0. rectangle.h ;
      stroke cr ;
      set_line_width cr 0.01 ;
      let i = ref (-5.) in
      let j = ref 5. in
      let c = ref 5. in
      while i <= c do
        move_to cr !i 0. ;
        line_to cr !i 5. ;
        line_to cr !i (-5.) ;
        move_to cr 0. !j ;
        line_to cr 5. !j ;
        line_to cr (-5.) !j ;
        i := !i +. 1. ;
        j := !j -. 1.
      done ;
      stroke cr ;
      set_line_width cr 0.05 ;
      set_source_rgba cr 1. 0.2 0.2 0.6 ;
      select_font_face cr "Purisa" ~slant:Upright ~weight:Normal |> ignore ;
      set_font_size cr 0.2 ;
      let i = ref (-5.) in
      let j = ref 5. in
      while i <= j do
        move_to cr !i (-0.1) ;
        line_to cr !i 0.1 ;
        move_to cr !i (-0.4) ;
        save cr ;
        scale cr 1. (-1.) ;
        show_text cr (string_of_float !i) ;
        restore cr ;
        move_to cr (-0.1) !i ;
        line_to cr 0.1 !i ;
        move_to cr (-0.4) !i ;
        save cr ;
        scale cr 1. (-1.) ;
        show_text cr (string_of_float !i) ;
        restore cr ;
        i := !i +. 1.
      done ;
      stroke cr ;
      false

    method plot_fun _ cr =
      set_line_width cr 0.05 ;
      let i = ref (-5.) in
      let j = ref 5. in
      while i < j do
        line_to cr !i (self#evaluate !i) ;
        i := !i +. 0.1
      done ;
      stroke cr ;
      true

    initializer
    table#set_halign `CENTER ;
    draw_area#misc#connect#draw ~callback:(self#init_graph draw_area)
    |> ignore ;
    function_box#draw_button#connect#released ~callback:(fun () ->
        draw_area#misc#queue_draw () ;
        draw_area#misc#connect#draw ~callback:(self#plot_fun draw_area)
        |> ignore)
    |> ignore
  end

let plot_frame ?packing ?show () = new plot_frame ?packing ?show ()
