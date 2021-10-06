open PrettyPrinter

exception Lexing_error of string

exception Wrong_eval_type of string

exception Variable_not_found of string

exception Wrong_variable_multiplicity of string

exception Unknown_derivative of string

exception Unknown_antiderivative of string

exception Unknown_command of string

exception Unknown_expression of string

let lexing_error message = raise @@ Lexing_error message

let wrong_type () =
  raise @@ Wrong_eval_type "A variable cannot occur inside an evaluation."

let unknown_antiderivative () =
  raise @@ Unknown_antiderivative "Cannot antiderive this expression."

let unknown_derivative () =
  raise @@ Unknown_derivative "Cannot derive this expression."

let unknown_command () =
  raise @@ Unknown_command "Cannot parse, unknown command."

let unknown_expression () =
  raise @@ Unknown_expression "Cannot parse, unknown expression."

let variable_not_found v e =
  raise
  @@ Variable_not_found
       ( "Variable (" ^ v ^ ") not found inside expression : "
       ^ string_of_expr e )

let multiplicity_error e =
  raise
  @@ Wrong_variable_multiplicity
       ("Too many variables inside : " ^ string_of_expr e)
