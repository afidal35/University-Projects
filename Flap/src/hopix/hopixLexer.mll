
{ (* -*- tuareg -*- *)
  open Lexing
  open Error
  open Position
  open HopixParser

  exception Invalid_char_literal

  let next_line_and f lexbuf  =
    Lexing.new_line lexbuf;
    f lexbuf

  let error lexbuf =
    error "during lexing" (lex_join lexbuf.lex_start_p lexbuf.lex_curr_p)


  let unescaped_char = function
  | "\\\\" -> '\\'
  | "\\'" -> '\''
  | "\\\"" -> '"'
  | "\\n" -> '\n'
  | "\\t" -> '\t'
  | "\\b" -> '\b'
  | "\\r" -> '\r'
  | s ->
      begin match s.[0] with
      | '\\' ->
          let s = String.sub s 1 (String.length s - 1) in
          begin try char_of_int (int_of_string s) with
          | Invalid_argument _ -> raise Invalid_char_literal
          end
      | c -> c
      end

}


let newline = ('\010' | '\013' | "\013\010")

let blank   = [' ' '\009' '\012']

let digit = ['0'-'9']

let digit = ['0'-'9']

let basic_id = ['a'-'z']['A'-'Z' 'a'-'z' '0'-'9' '_']*

let constr_id = ['A'-'Z']['A'-'Z' 'a'-'z' '0'-'9' '_']*

let type_variable = '`'['a'-'z']['A'-'Z' 'a'-'z' '0'-'9' '_']*

let hexa_prefix = '0' ['x']

let bin_prefix = '0' ['b']

let octal_prefix = '0' ['o']

let hexa_digit = digit | ['a'-'f' 'A'-'F']

let bin_digit = ['0' '1']

let octal_digit = ['0'-'7']

let int = '-'? digit+ | hexa_prefix hexa_digit+ | octal_prefix octal_digit+ | bin_prefix bin_digit+

let printable = [' '-'~']

let esc_char = ['\\' '\'' '"' 'n' 't' 'b' 'r']

let esc_seq = '\\' (digit+ | hexa_prefix hexa_digit+ | esc_char)

let char = esc_seq | printable # '\''

let string = esc_seq | printable # '"' 

(*
let atom = (['\000' - '\255'] | "\\0" ['x']? hexa_digit hexa_digit | printable | '\\' esc_char)

let char = '\'' atom '\''

let string = '"' (atom | '\'' | '\"')* '"'
*)

(* Look for string *)

rule token = parse
  (** Layout *)
  | newline            { next_line_and token lexbuf }
  | blank+             { token lexbuf               }
  | eof                { EOF }

    (** Keywords *)
  | "type"   { TYPE }
  | "extern" { EXTERN }
  | "fun"    { FUN }
  | "let"    { LET }
  | "and"    { AND }
  | "if"     { IF }
  | "else"   { ELSE }
  | "ref"    { REF }
  | "switch" { SWITCH }
  | "while"  { WHILE }
  | "do"     { DO }
  | "for"    { FOR }
  | "to"     { TO }
  | "in"     { IN }


  (** Operators *)
  | '+'  { PLUS }
  | '-'  { MINUS }
  | '*'  { TIMES }
  | '/'  { DIV }
  | "&&" { LAND }
  | "||" { LOR }
  | "="  { EQ }
  | "=?" { EQQMARK }
  | "<=?" { LEQQMARK }
  | ">=?" { GEQQMARK }
  | "<?" { LQMARK }
  | ">?" { GQMARK }
  | "<"  { LT }
  | ">"  { GT }


    (** Punctuation *)
  | '('  { LPAREN }
  | ')'  { RPAREN }
  | '['  { LBRACKET }
  | ']'  { RBRACKET }
  | '{'  { LBRACE }
  | '}'  { RBRACE }
  | ':'  { COLON }
  | ','  { COMMA }
  | ';'  { SEMICOLON }
  | '\\' { BACKSLASH }
  | '.'  { DOT }
  | '!'  { EMARK }
  | '|'  { PIPE }
  | '&'  { AMPERSAND }
  | '_'  { UNDERSCORE }
  | "->" { ARROW }
  | ":=" { COLONEQ }


  (** Literals *)
  | int as n
    {
      try INT (Mint.of_string n) with
      | Failure _ ->  global_error "during parsing" "Syntax error."
    }
  | '\'' (char as c) '\''
      {
        try CHAR (unescaped_char c) with
        | Invalid_char_literal -> error lexbuf ""
      }
  | '"' (string* as s) '"'
      {
        try STRING (Scanf.unescaped s) with
        | Scanf.Scan_failure _ -> error lexbuf "Invalid string literal."
      }
  | '"' (string*) 
    {
      Error.error "during lexing" (lex_join (Position.end_of_position (Position.cpos lexbuf)) (Position.end_of_position (Position.cpos lexbuf))) "Unterminated string."
    }

  (** Identifiers *)
  | basic_id as id    { BASIC_ID id }
  | constr_id as id   { CONSTR_IDENT id }
  | type_variable as id { TYPE_VAR id }

  (** Comments *)
  | "//" {line_comment lexbuf}
  | "/*" {block_comment 0 lexbuf}

  (** Lexing error. *)
  | _               { error lexbuf "unexpected character." }

and line_comment = parse
  | newline {next_line_and token lexbuf}
  | eof {EOF}
  | _ {line_comment lexbuf}

and block_comment depth = parse
  | "/*" {block_comment (succ depth) lexbuf}
  | "*/"
      { if depth = 0 then token lexbuf else block_comment (pred depth) lexbuf }
  | eof {error lexbuf "Unterminated comment."}
  | _ {block_comment depth lexbuf}