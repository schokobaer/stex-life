grammar StexLifeGrammar;

@header {
	package at.apf.stexlife.parser.antlr4;
}

/**
 * Parser Rules
 */

program:	    include* function* ;

include:        'from' includeSource 'import' includeDeclaration (',' includeDeclaration)* ';';
includeDeclaration: ID ('as' ID)? ;
includeSource:  (ID|STRING) ;

function:       (EXPORT)? ID '(' paramList ')' '{' stmt* '}' ;
paramList:      (ID (',' ID)*)? ;

stmt:           (block|ifStmt|tryStmt|whileStmt|forStmt|foreachStm|throwStmt|returnStmt
					 |assignStmt ';'|declareStmt|voidFunctionCall);

block:          '{' stmt* '}' ;

ifStmt:         'if' '(' expression ')' block (elseIfStmt)* (elseBlock)? ;
elseIfStmt:     'elseif' '(' expression ')' block ;
elseBlock:      'else' block ;

tryStmt:        'try' block catchBlock (finallyBlock)? ;
catchBlock:	    'catch' '(' ID ')' block ;
finallyBlock:   'finally' block ;

whileStmt:		'while' '(' expression ')' block ;

forStmt:        'for' '(' LET ID '=' expression ';' expression ';' assignStmt ')' block ;
foreachStm:     'foreach' '(' LET ID 'in' expression ')' block ;

throwStmt:		'throw' expression ';' ;
returnStmt:	    'return' (expression)? ';' ;

assignStmt: 	assignee '=' expression ;
declareStmt:		(LET|CONST) ID ('=' expression)? ';' ;

voidFunctionCall:		functionCall ';' ;

expression:      ('(' expression ')'|/*concatExpression*/|operation|operand|dynamicAccess|array|object|functionCall|anonymousFunction) ;
operationExpression:	('(' expression ')'|operand|dynamicAccess|array|object|functionCall) ;
//concatExpression: (functionCall|array|dynamicAccess|object) '.' expression;

operand:		(identifier|value);
identifier:		(ID|SELF) ('.' ID)* ;
value:			(INT|FLOAT|BOOLEAN|NULL|STRING) ;
dynamicAccess:	identifier '[' expression ']' ;

object:				'{' (objectField (',' objectField)*)? '}';
objectField:		ID (':' expression)? ;

array:				'[' (expression (',' expression)*)? ']' ;

anonymousFunction:  '(' paramList ')' '{' stmt* '}' ;

functionCall:		identifier '(' argList ')' ;
argList:            (expression (',' expression)*)? ;

operation:		    operationExpression operationType expression
					| notOperation ;
notOperation:		'not' expression ;
operationType:		(ADD|SUB|MUL|DIV|MOD|EQU|NEQ|GRT|GRE|SMT|SME|IN|AND|OR) ;

assignee:			identifier | dynamicAccess ;



/*
 * Lexer Rules
 */
WHITESPACE	:	[ \t\r\n]+ -> skip ;
COMMENT		:	'/*' .*? '*/' -> skip ;

INT         :   ('-')?([0-9])+ ;
FLOAT		:	('-')?([0-9])+ ('.' ([0-9])+ )? ;
BOOLEAN		:	('true' | 'false') ;
NULL		:	'null' ;
STRING		:	'"' (. | '\"')*? '"' ;

LET         : 'let' ;
CONST       : 'con' ;
SELF        : 'self' ;
EXPORT      : 'export' ;

ADD : '+' ;
SUB : '-' ;
MUL : '*' ;
DIV : '/' ;
MOD : '%' ;
EQU : '==' ;
NEQ : '!=' ;
GRT : '>' ;
GRE : '>=' ;
SMT : '<' ;
SME : '<=' ;
IN : 'in' ;
AND : '&&' ;
OR : '||' ;

ID			:	([A-Z]|[a-z])+([A-Z]|[a-z]|[0-9])* ;