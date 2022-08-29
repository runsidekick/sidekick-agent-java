grammar Condition;

parse
 : expression EOF
 ;

expression
 : LPAREN expression RPAREN                       #parenExpression
 | left=expression op=binary right=expression     #binaryExpression
 | left=operand op=comparator right=operand       #comparatorExpression
 ;

comparator
 : GT | GE | LT | LE | EQ | NE
 ;

binary
 : AND | OR
 ;

BOOLEAN
 : TRUE | FALSE
 ;

operand
 : BOOLEAN | CHARACTER | NUMBER | STRING | NULL | VARIABLE | PLACEHOLDER
 ;

AND         : 'AND' | '&&';
OR          : 'OR' | '||';
NOT         : 'NOT' ;
TRUE        : 'true' ;
FALSE       : 'false' ;
NULL        : 'null' ;
GT          : '>' ;
GE          : '>=' ;
LT          : '<' ;
LE          : '<=' ;
EQ          : '==' ;
NE          : '!=' ;
LPAREN      : '(' ;
RPAREN      : ')' ;
CHARACTER   : '\'' . '\'' ;
NUMBER      : '-'? [0-9]+ ( '.' [0-9]+ )? ;
STRING      : '"' (~('"' | '\\' | '\r' | '\n') | '\\' ('"' | '\\'))* '"' ;
VARIABLE    : [a-zA-Z_][a-zA-Z0-9_.]* ;
PLACEHOLDER : '$' '{' [a-zA-Z0-9_.]+ '}' ;
WS          : [ \r\t\u000C\n]+ -> skip ;
