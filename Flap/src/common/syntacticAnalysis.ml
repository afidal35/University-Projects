let parsing_step = "during parsing"

let process ~lexer_init ~lexer_fun ~parser_fun ~input =
  parser_fun lexer_fun (lexer_init input)

let process ~lexer_init ~lexer_fun ~parser_fun ~input  = 
  process ~lexer_init ~lexer_fun ~parser_fun ~input