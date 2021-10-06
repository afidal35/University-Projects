%{ (* -*- tuareg -*- *)

  open HopixAST
  open Position

let none_to_list = function
  | None -> []
  | Some l -> l

(* [flatten_patterns extract_patterns ps] *inlines* the pattern
   lists extracted from the given pattern list [ps] via the
   function [extract_patterns].  It returns the resulting list.  *)
let flatten_patterns extract_patterns ps =
  (* [patterns p acc] extends [acc] with the list of patterns
     composing the pattern [p].  *)
  let patterns p acc =
    match extract_patterns (Position.value p) with
    | Some ps -> ps :: acc
    | None -> [p] :: acc
  in
  List.(flatten (fold_right patterns ps []))

(* Build a pattern from a pattern list with the given wrapper
   function.  *)
let wrap_patterns wrapper = function
  | [] -> assert false
  | [p] -> Position.value p
  | ps -> wrapper ps

let por ps =
  let extract_patterns = function
    | POr x -> Some x
    | _ -> None
  in
  wrap_patterns (fun ps -> POr ps) (flatten_patterns extract_patterns ps)

let pand ps =
  let extract_patterns = function
    | PAnd x -> Some x
    | _ -> None
  in
  wrap_patterns (fun ps -> PAnd ps) (flatten_patterns extract_patterns ps)

%}

%token EOF
%token TYPE EXTERN FUN AND IF ELSE REF WHILE SWITCH DO IN FOR TO LET
%token PLUS MINUS TIMES DIV LAND LOR EQ LT GT EQQMARK LEQQMARK GEQQMARK LQMARK GQMARK
%token LPAREN RPAREN LBRACKET RBRACKET LBRACE RBRACE
%token COLON COMMA SEMICOLON BACKSLASH EMARK PIPE AMPERSAND UNDERSCORE
%token ARROW COLONEQ DOT

%token<Int64.t> INT
%token<char>    CHAR
%token<string>  STRING
%token<string>  VAR_IDENT CONSTR_IDENT LABEL_IDENT TYPE_CON TYPE_VAR

%nonassoc ELSE
%nonassoc PIPE
%nonassoc AMPERSAND
%right SEMICOLON
%left DOT
%right COLONEQ
%left ARROW
%left LOR
%left LAND
%left LT GT EQ LQMARK GQMARK LEQQMARK GEQQMARK EQQMARK
%left PLUS MINUS
%left TIMES DIV
%nonassoc REF
%nonassoc EMARK


%start<HopixAST.t> program

%%

program:
  | p = located(definition)* EOF { p }

definition:
  | TYPE tc = located(type_constructor)
    tv = less_comma_nonempty_list(located(type_variable))?
    td = preceded(EQ, type_definition)?
      {
        let td =
          match td with
          | None -> Abstract
          | Some td -> td
        in
        DefineType(tc, none_to_list tv, td)
      }
  | EXTERN ext_id = located(identifier) COLON ty_name = located(type_scheme)
      { DeclareExtern (ext_id, ty_name) }
  | vd = value_definition { DefineValue(vd) }

type_definition:
  | cdl = separated_nonempty_list(PIPE, constr_definition)
      { DefineSumType cdl }
  | lct = paren_comma_nonempty_list(label_colon_type)
    { DefineRecordType lct }

label_colon_type:
  | lab = located(label) COLON t = located(ty)
    { (lab, t ) }

constr_definition:
  | c = located(constructor) 
    tl = paren_comma_nonempty_list(located(ty))?
      { (c, none_to_list tl) }

value_definition:
  | LET id = located(identifier) ts = preceded(COLON, located(type_scheme))? EQ expr = located(expression) 
    {
      SimpleValue(id, ts, expr)
    }
  | FUN f = function_definition f_list = preceded(AND, function_definition)*
    {
      RecFunctions(f :: f_list)
    }

function_definition:
  | ts = preceded(COLON, located(type_scheme))? id = located(identifier) p = located(pattern) EQ expr = located(expression)
    {
      (id, ts, FunctionDefinition(p, expr))
      (*(id, None, FunctionDefinition(p, expr))*)
    }

label_pattern:
  | COMMA id = located(label) EQ p = located(pattern) 
    {(id, p)}

atom_pattern:
  | id = located(identifier)
    {PVariable(id)}
  | lit = located(literal)
    {PLiteral(lit)}
  | UNDERSCORE
    {PWildcard}

(* pattern who are not conjonction nor disjonction *)
pattern_simple:
  | p = atom_pattern {p}
  | p = located(pattern_simple) COLON t = located(ty_simple) (** SHIFT REDUCE **) (**ERROR FOR 108 - 109**)
    {PTypeAnnotation(p, t)}
  | p_list = paren_comma_nonempty_list(located(pattern))
    {PTuple(p_list)}
  | con = located(constructor) t_list = delimited(LT, separated_list(COMMA, located(ty)), GT)? p_list = delimited(LPAREN, paren_comma_nonempty_list(located(pattern)), RPAREN)?
    {PTaggedValue(con, t_list, none_to_list p_list)} 
  | LBRACE id = located(label) EQ p = located(pattern)  l_p_list = label_pattern* RBRACE t_list = delimited(LT, separated_list(COMMA, located(ty)), GT)?
    {
      PRecord((id, p) :: l_p_list, t_list)
    }

pattern_conj:
  | p_list = separated_nonempty_list(AMPERSAND, located(pattern_simple)) { pand p_list }

pattern:
  | p_list = separated_nonempty_list(PIPE, located(pattern_conj)) { por p_list }


label_expression:
  | id = located(label) EQ expr = located(expression) {(id, expr)}  

expression:
  | lit = located(literal) { Literal lit }
  | id = located(identifier) t_list = less_comma_nonempty_list(located(ty))?
    {
      Variable(id, t_list)
    }
  | id = located(constructor) t_list = less_comma_nonempty_list(located(ty))? expr_list = paren_comma_nonempty_list(located(expression))?
    {
      Tagged(id, t_list, none_to_list expr_list)
    }
  | expr_list = paren_comma_nonempty_list(located(expression)) (** SHIFT REDUCE **)
    {Tuple(expr_list)}
  | LBRACE l_e_list = separated_nonempty_list(COMMA, label_expression) RBRACE t_list = less_comma_nonempty_list(located(ty))?
    {
      Record(l_e_list, t_list)
    }
  | expr = located(expression) DOT id = located(label)
    {Field(expr, id)}
  | expr1 = located(expression) SEMICOLON expr2 = located(expression)
    {Sequence(expr1 :: expr2 :: [])}
  | vdef = value_definition SEMICOLON expr = located(expression)
    {
      Define(vdef, expr)
    }
  | BACKSLASH p = located(pattern) ARROW expr = located(expression)
    {Fun(FunctionDefinition(p , expr))}
  (*| expr1 = located(expression) expr2 = located(expression)
    {Apply(expr1, expr2)}*) (** conflict 106 , also conflict with -INT **)
  | SWITCH LPAREN expr = located(expression) RPAREN LBRACE br = branches RBRACE
    {
      Case(expr, br)
    }
  | IF LPAREN expr1 = located(expression) RPAREN LBRACE expr2 = located(expression) RBRACE ELSE LBRACE expr3 = located(expression) RBRACE 
    {IfThenElse(expr1, expr2, expr3)}
  | REF expr = located(expression)
    {Ref(expr)}
  | expr1 = located(expression) COLONEQ expr2 = located(expression)
    {Assign(expr1, expr2)}
  | EMARK expr = located(expression)
    {Read(expr)}
  | WHILE LPAREN expr1 = located(expression) RPAREN LBRACE expr2 = located(expression) RBRACE
    {While(expr1, expr2)}
  | DO LBRACE expr2 = located(expression) RBRACE WHILE LPAREN expr1 = located(expression) RPAREN
    {While(expr1, expr2)}
  | FOR id = located(identifier) IN LPAREN expr1 = located(expression) TO expr2 = located(expression) RPAREN LBRACE expr3 = located(expression) RBRACE
    {For(id, expr1, expr2, expr3)} 
  | LPAREN expr = expression RPAREN
    {expr}
  | LPAREN expr = located(expression) COLON t = located(ty) RPAREN
    {TypeAnnotation(expr, t)}
  | b = binop_expression(expression) { b }

binop_expression(right_expression):
  | e1 = located(expression) b = binop e2 = located(right_expression)
    {
      Apply(
        { value=Apply(
          { value=Variable(
            { value=b
              ; position=e1.position 
            }, None)
            ; position=e1.position 
          },
          e1)
        ; position=e1.position 
        },
      e2)
    }


(* branche/branches *)
branches:
  | PIPE? blist = separated_nonempty_list(PIPE, located(branch))
      { blist }

%inline branch:
  | p = located(pattern) ARROW e = located(expression) { Branch(p, e) }

(** Types / Type scheme *)

ty_simple:
  | tc = type_constructor 
    t_list = less_comma_nonempty_list(located(ty))?
      { TyCon (tc, none_to_list t_list) }
  | tv = type_variable {TyVar(tv)}
  | LPAREN t=ty RPAREN {t} 

ty:
  | list_t = separated_nonempty_list(TIMES, located(ty_simple)) (** TO CORRECT CREATING EPSILON CYCLE **)
    {
      TyTuple(list_t)
    }
  | t1 = located(ty_simple) ARROW
    t2 = located(ty)
      { TyArrow (t1, t2) }

type_scheme_list:
  | LBRACKET var_list = nonempty_list(located(type_variable)) RBRACKET {var_list}

type_scheme: 
  | var_list = option(type_scheme_list)
    t = located(ty)
      { 
        ForallTy(none_to_list var_list, t) 
      }

(** Binary operators *)

%inline binop:
  | PLUS { Id("`+`") }
  | MINUS { Id("`-`") }
  | TIMES { Id("`*`") }
  | DIV { Id("`/`") }
  | LAND { Id("`&&`") }
  | LOR { Id("`||`") }
  | EQQMARK { Id("`=?`") }
  | LEQQMARK { Id("`<=?`") }
  | GEQQMARK { Id("`>=?`") }
  | LQMARK { Id("`<?`") }
  | GQMARK { Id("`>?`") }

%inline identifier:
  | id = VAR_IDENT { Id id }

%inline label:
  | id = LABEL_IDENT { LId id }

%inline constructor:
  | id = CONSTR_IDENT { KId id }

%inline type_constructor:
  | id = TYPE_CON { TCon id }

%inline type_variable:
  | id = TYPE_VAR { TId id }

%inline literal:
  | i = INT { LInt i }
  | MINUS i = INT { LInt (Int64.neg i) }
  | c = CHAR { LChar c }
  | s = STRING { LString s }

(** Delim separator *)
%inline delim_sep_nonempty_list(opening, separator, X, closing):
  | opening l = separated_nonempty_list(separator, X) closing { l }

(** Delim , *)
%inline delim_comma_nonempty_list(opening, X, closing):
  | l = delim_sep_nonempty_list(opening, COMMA, X, closing) { l }

(** Delim < , , ..> *)
%inline less_comma_nonempty_list(X):
  | l = delim_comma_nonempty_list(LT , X , GT) { l }

(** Delim ( , , ..) *)
%inline paren_comma_nonempty_list(X):
  | l = delim_comma_nonempty_list(LPAREN, X, RPAREN) { l }

(** Delim { , , ..} *)
%inline bracket_comma_nonempty_list(X):
  | l = delim_comma_nonempty_list(LBRACKET, X, RBRACKET) { l }

%inline located(X): x=X {
  Position.with_poss $startpos $endpos x
}