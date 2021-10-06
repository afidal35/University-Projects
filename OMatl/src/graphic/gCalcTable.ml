let calc_ops =
  [| [|"(x)"; "("; ")"; "x^y"|]
   ; [|"7"; "8"; "9"; "/"|]
   ; [|"4"; "5"; "6"; "*"|]
   ; [|"1"; "2"; "3"; "-"|]
   ; [|"0"; "."; "exp"; "+"|] |]

let calc_extensions =
  [| [|"sin"; "cos"|]
   ; [|"asin"; "acos"|]
   ; [|"log"; "tan"|]
   ; [|"sqrt"; "atan"|]
   ; [|"pi"; "e"|] |]

let tools = [|"AC"; "DEL"|]

class calc_base_table ~command ?packing ?show () =
  let table_ops =
    GPack.table ~rows:5 ~columns:4 ~homogeneous:true ~show:true ()
  in
  object
    initializer
    for i = 0 to 4 do
      for j = 0 to 3 do
        let button =
          GButton.button
            ~label:("  " ^ calc_ops.(i).(j) ^ "  ")
            ~packing:
              (table_ops#attach ~xpadding:4 ~ypadding:3 ~top:(i + 1) ~left:j)
            ()
        in
        button#connect#clicked ~callback:(fun () -> command calc_ops.(i).(j))
        |> ignore
      done
    done ;
    GObj.pack_return table_ops ~packing ~show |> ignore
  end

let calc_base_table ~command ?packing ?show () =
  new calc_base_table ~command ?packing ?show ()

class calc_ext_table ~command ?packing ?show () =
  let table_ext =
    GPack.table ~rows:4 ~columns:2 ~homogeneous:true ~show:true ()
  in
  object
    initializer
    for i = 0 to 4 do
      for j = 0 to 1 do
        let button =
          GButton.button
            ~label:("  " ^ calc_extensions.(i).(j) ^ "  ")
            ~packing:
              (table_ext#attach ~xpadding:4 ~ypadding:3 ~top:(i + 1) ~left:j)
            ()
        in
        button#connect#clicked ~callback:(fun () ->
            command calc_extensions.(i).(j))
        |> ignore
      done
    done ;
    GObj.pack_return table_ext ~packing ~show |> ignore
  end

let calc_ext_table ~command ?packing ?show () =
  new calc_ext_table ~command ?packing ?show ()

class calc_tools_table ~command ?packing ?show () =
  let table_tools =
    GPack.table ~rows:1 ~columns:2 ~homogeneous:true ~show:true ()
  in
  object
    initializer
    for i = 0 to 1 do
      let button =
        GButton.button
          ~label:("  " ^ tools.(i) ^ "  ")
          ~packing:
            (table_tools#attach ~xpadding:3 ~ypadding:3 ~top:0 ~left:i
               ~expand:`BOTH)
          ()
      in
      button#connect#clicked ~callback:(fun () -> command tools.(i))
      |> ignore
    done ;
    GObj.pack_return table_tools ~packing ~show |> ignore
  end

let calc_tools_table ~command ?packing ?show () =
  new calc_tools_table ~command ?packing ?show ()
