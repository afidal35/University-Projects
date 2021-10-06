%{ (* -*- tuareg -*- *)

  open HopixAST
  open Position

let none_to_list = function
  | None -> []
  | Some l -> l

let map_pos x y = Position.map (fun _ -> x) y

let flatten_patterns extract_patterns ps =
  let patterns p acc =
    match extract_patterns (Position.value p) with
    | Some ps -> ps :: acc
    | None -> [p] :: acc
  in
  List.(flatten (fold_right patterns ps []))

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

%token<Mint.t> INT
%token<char>    CHAR
%token<string>  STRING
%token<string>  BASIC_ID CONSTR_IDENT TYPE_VAR

%left ARROW
%nonassoc ELSE
%nonassoc PIPE
%nonassoc AMPERSAND
%right SEMICOLON
%left DOT
%right COLONEQ
%left LOR
%left LAND
%left EQ LQMARK GQMARK LEQQMARK GEQQMARK EQQMARK
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
  | vd = value_definition(expression) { DefineValue(vd) }

type_definition:
  | PIPE? cdl = separated_nonempty_list(PIPE, constr_definition)
    { DefineSumType cdl }
  | LBRACE lct = separated_nonempty_list(COMMA,separated_pair(located(label),COLON,located(ty))) RBRACE
    { DefineRecordType lct }

constr_definition:
  | c = located(constructor) 
    tl = paren_comma_nonempty_list(located(ty))?
      { (c, none_to_list tl) }

value_definition(EXPR):
  | LET id = located(identifier) ts = preceded(COLON, located(type_scheme))? EQ expr = located(EXPR) 
    { SimpleValue(id, ts, expr) }
  | FUN f_list = separated_nonempty_list(AND,function_definition(EXPR))
    { RecFunctions(f_list) }

function_definition(EXPR):
  | ts = preceded(COLON, located(type_scheme))? id = located(identifier) p = located(pattern) EQ expr = located(EXPR)
    { (id, ts, FunctionDefinition(p, expr)) }
  | ts = preceded(COLON, located(type_scheme))? id = located(identifier) p = located(pattern) ARROW expr = located(EXPR)
    { (id, ts, FunctionDefinition(p, expr)) }

atom_pattern:
  | id = located(identifier)
    { PVariable id }
  | lit = located(literal)
    { PLiteral lit }
  | UNDERSCORE
    { PWildcard }

(* pattern who are not conjonction nor disjonction *)
pattern_simple:
  | p = atom_pattern { p }
  | con = located(constructor) 
    t_list = delimited(LT, loption(separated_nonempty_list(COMMA, located(ty))), GT)? 
    p_list = paren_comma_nonempty_list(located(pattern))?
    { PTaggedValue(con, t_list,none_to_list p_list) } 
  | p_list = paren_comma_nonempty_list(located(pattern))
    {
      match p_list with
      | [p] -> p.value
      | _ -> PTuple(p_list)
    }
  | p = located(pattern_simple) COLON t = located(ty_simple)
    { PTypeAnnotation(p, t) }
  | LBRACE lp = separated_nonempty_list(COMMA,separated_pair(located(label),EQ,located(pattern))) RBRACE 
    ty_list = delimited(LT,loption(separated_nonempty_list(COMMA,located(ty))),GT)?
    { PRecord(lp, ty_list) }

pattern_conj:
  | p_list = separated_nonempty_list(AMPERSAND, located(pattern_simple)) { pand p_list }

pattern:
  | p_list = separated_nonempty_list(PIPE, located(pattern_conj)) { por p_list }

label_expression:
  | id = located(label) EQ expr = located(simple_expression) {(id, expr)}  

base_base_simple_expression:
  | lit = located(literal) { Literal lit }
  | id = located(identifier) t_list = delimited(LT, loption(separated_nonempty_list(COMMA, located(ty))), GT)?
    { Variable(id, t_list) }
  | LPAREN expr = located(expression) COLON t = located(ty) RPAREN
    { TypeAnnotation(expr, t) }
  | expr_list = paren_comma_nonempty_list(located(expression)) 
    { 
      match expr_list with
      | [e] -> e.value
      | _ -> Tuple(expr_list)
    }

base_simple_expression:
  | bbs = base_base_simple_expression { bbs }
  | id = located(constructor) 
    t_list = delimited(LT, loption(separated_nonempty_list(COMMA, located(ty))), GT)?
    expr_list = paren_comma_nonempty_list(located(expression))?
    { Tagged(id, t_list, none_to_list expr_list) }
  | e1 = located(base_base_simple_expression) e2 = located(base_simple_expression)
    { Apply(e1, e2) }

simple_expression:
  | b = base_simple_expression { b }
  | REF expr = located(base_base_simple_expression)
    { Ref(expr) }
  | EMARK expr = located(simple_expression)
    { Read(expr) }
  | LBRACE l_e_list = separated_nonempty_list(COMMA, label_expression) RBRACE 
    t_list = delimited(LT, loption(separated_nonempty_list(COMMA, located(ty))), GT)?
    { Record(l_e_list, t_list) }
  | b = binop_expression(simple_expression) { b }
  | BACKSLASH p = located(pattern) ARROW expr = located(simple_expression)
    { Fun(FunctionDefinition(p, expr)) }
  | expr1 = located(simple_expression) COLONEQ expr2 = located(simple_expression)
    { Assign(expr1, expr2) }
  | WHILE LPAREN expr1 = located(expression) RPAREN LBRACE expr2 = located(expression) RBRACE
    { While(expr1, expr2) }
  | DO LBRACE expr2 = located(expression) RBRACE w=located(WHILE) LPAREN expr1 = located(expression) RPAREN
    { Sequence [expr2; {position = w.position; value = While (expr1, expr2)}] }
  | SWITCH LPAREN expr = located(simple_expression) RPAREN LBRACE br = branches RBRACE
    { Case(expr, br) }
  | FOR id = located(identifier) IN LPAREN 
    expr1 = located(expression) TO expr2 = located(expression) RPAREN LBRACE 
    expr3 = located(expression) RBRACE
    { For(id, expr1, expr2, expr3) }
  | expr = located(simple_expression) DOT id = located(label)
    { Field(expr, id) }
  | cnd = conditional_expression(inlined_simple_expression) 
    { cnd }

local_definition:
  | vdef = value_definition(simple_expression) SEMICOLON expr = located(expression)
    { Define(vdef, expr) }

unsequence_expression:
  | se = simple_expression { se }
  | ld = local_definition { ld }

sequence_expression:
  | expr1 = located(expression) SEMICOLON expr2 = located(expression)
    { Sequence[expr1; expr2] }

conditional_expression(right_expression):
  | IF LPAREN expr1 = located(expression) RPAREN LBRACE 
    expr2 = located(inlined_simple_expression) RBRACE ELSE LBRACE 
    expr3 = located(right_expression) RBRACE 
    { IfThenElse(expr1, expr2, expr3) }

%inline inlined_simple_expression:
  | e = simple_expression 
    { e }

binop_expression(right_expression):
  | e1 = located(simple_expression) b = binop e2 = located(right_expression)
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

expression:
  | e = unsequence_expression 
  | e = sequence_expression
  | e = conditional_expression(local_definition) 
  | e = binop_expression(local_definition)
    { e }

(* branche/branches *)
branches:
  | PIPE? blist = separated_nonempty_list(PIPE, located(branch))
      { blist }

%inline branch:
  | p = located(pattern) ARROW e = located(expression) 
    { Branch(p, e) }

(** Types / Type scheme *)

ty_very_simple:
  | tv = type_variable {TyVar(tv)}
  | LPAREN t = ty RPAREN {t}
  | tc = type_constructor 
    t_list = loption(less_comma_nonempty_list(located(ty)))
      { TyCon (tc, t_list) }

ty_simple:
  | tvs = ty_very_simple { tvs }
  | t = located(ty_very_simple) list_t = preceded(TIMES,separated_nonempty_list(TIMES, located(ty_very_simple)))
    { TyTuple(t :: list_t) }

ty:
  | t = ty_simple
    {t}
  | t1 = located(ty_simple) ARROW
    t2 = located(ty)
      { TyArrow (t1, t2) }

type_scheme: 
  | var_list = loption(delimited(LBRACKET,nonempty_list(located(type_variable)),RBRACKET))
    t = located(ty)
      { ForallTy(var_list, t) }

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
  | id = BASIC_ID { Id id }

%inline label:
  | id = BASIC_ID { LId id }

%inline type_constructor:
  | id = BASIC_ID { TCon id }

%inline constructor:
  | id = CONSTR_IDENT { KId id }

%inline type_variable:
  | id = TYPE_VAR { TId id }

%inline literal:
  | i = INT { LInt i }
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