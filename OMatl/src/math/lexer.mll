{
  open Parser
  open Syntax
  open Error
  
  let op0_table = Hashtbl.create 2;;
  List.iter (fun (kwd, tok) -> Hashtbl.add op0_table kwd tok)
              [ ("e", E);
                ("pi", Pi)]
                
  let op1_table = Hashtbl.create 10;;
  List.iter (fun (kwd, tok) -> Hashtbl.add op1_table kwd tok)
              [ ("sqrt", Sqrt);
                ("exp", Exp);
                ("log", Log);
                ("sin", Sin);
                ("cos", Cos);
                ("tan", Tan);
                ("asin", ASin);
                ("acos", ACos);
                ("atan", ATan)]
}

let digit = ['0'-'9']
let frac = '.' digit*
let exp = ['e' 'E'] ['-' '+']? digit+
let float = digit* frac? exp?
(* let int = '-'? ['0'-'9'] ['0'-'9']* *)

rule token = parse
  | [' ' '\t']          { token lexbuf }
  | '\n'                { EOL }
  | float as f          { FLOAT (float_of_string f) }
  | '+'                 { PLUS }
  | '*'                 { TIMES }
  | '-'                 { MINUS }
  | '/'                 { DIV }
  | '^'                 { EXPO }
  | '('                 { LPARA }
  | ')'                 { RPARA }
  | ','                 { COL }
  | "eval"              { EVAL }
  | "subst"             { SUBST }
  | "simpl"             { SIMPL }
  | "derive"            { DERIVE }
  | "solve"             { SOLVE }
  | "antiderive"        { ANTIDERIVE }
  | "plot"              { PLOT }
  | "integ"             { INTEG }
  | ['a'-'z']* as v     { try OP0(Hashtbl.find op0_table v)
                          with Not_found ->
                             try OP1(Hashtbl.find op1_table v)
                             with Not_found -> IDENT v
                        }
  | _       { lexing_error (Lexing.lexeme lexbuf) }
