open GMain
open GdkKeysyms
open Graphic

let main () =
  GMain.init () |> ignore ;
  let main_window =
    GWindow.window ~border_width:10 ~resizable:true ~position:`CENTER
      ~title:"OMATL" ()
  in
  ignore (main_window#connect#destroy ~callback:Main.quit) ;
  main_window#misc#modify_bg [(`NORMAL, `BLACK)] ;
  main_window#misc#modify_fg [(`NORMAL, `WHITE)] ;
  let v_box =
    GPack.vbox ~spacing:10 ~homogeneous:false ~packing:main_window#add ()
  in
  let menubar = GMenu.menu_bar ~packing:v_box#pack () in
  let factory = new GMenu.factory menubar in
  let accel_group = factory#accel_group in
  let file_menu = factory#add_submenu "Options" in
  let factory = new GMenu.factory file_menu ~accel_group in
  ignore (factory#add_item "Quit" ~key:_Q ~callback:Main.quit) ;
  let table = GPack.table ~rows:1 ~columns:5 ~packing:v_box#pack () in
  table#set_halign `CENTER ;
  GCalcNotebook.calc_notebook
    ~packing:(table#attach ~left:0 ~right:6 ~top:0)
    ()
  |> ignore ;
  main_window#show () ;
  GMain.main ()

let _ = main ()
