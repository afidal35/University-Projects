%token <float> FLOAT
%token <string> IDENT
%token <Syntax.op0> OP0
%token <Syntax.op1> OP1
%token PLUS MINUS TIMES DIV EXPO LPARA RPARA EOL COL
%token EVAL SUBST SIMPL SOLVE ANTIDERIVE DERIVE INTEG PLOT
%start <Syntax.expression> expression
%left PLUS MINUS
%left TIMES DIV
%right EXPO
%start <Syntax.command> command
%{ 
  open Syntax 
  open Utils
%}
%%

expression: e=expr EOL { e }

expr:  
  |    f=FLOAT                           { Leaf(Poly(poly_of_float f)) }
  |    v=IDENT                           { Leaf(Poly(poly_of_var v)) }
  |    op0=OP0                           { Leaf(App0(op0)) }
  |    op1=OP1 LPARA   e=expr  RPARA     { App1(Light.one, op1, e, Light.one) }
  |    MINUS   e=expr                    { App1(Light.one, UMinus, e, Light.one) }
  |    e1=expr PLUS    e2=expr           { App2(Plus, [e1; e2]) }
  |    e1=expr MINUS   e2=expr           { App2(Minus, [e1; e2]) }
  |    e1=expr TIMES   e2=expr           { App2(Mult, [e1; e2]) }
  |    e1=expr DIV     e2=expr           { App2(Div, [e1; e2]) }
  |    e1=expr EXPO    e2=expr           { App2(Expo, [e1; e2]) }
  |    LPARA   e=expr  RPARA             { e }

command: c=cmd EOL { c }

cmd:
  | EVAL       LPARA      e=expr  RPARA                                                     { Evaluate e }
  | SUBST      LPARA      e1=expr COL    v=IDENT COL   e2=expr    RPARA                     { Substitute(e1, v, e2) }
  | SIMPL      LPARA      e=expr  RPARA                                                     { Simplify(e) }
  | SOLVE      LPARA      e=expr  COL    v=IDENT RPARA                                      { Solve(e, v) }
  | DERIVE     LPARA      e=expr  COL    v=IDENT RPARA                                      { Derive(e, v) }
  | ANTIDERIVE LPARA      e=expr  COL    v=IDENT RPARA                                      { Antiderive(e, v) }
  | PLOT       LPARA      e=expr  COL    v=IDENT RPARA                                      { Plot(e, v) }
  | INTEG      LPARA      e=expr  COL    v=IDENT COL   a=expr     COL    b=expr RPARA       { Integrate(e, v, a, b) }

