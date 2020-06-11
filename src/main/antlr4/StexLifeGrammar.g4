grammar StexLifeGrammar;

@header {
	package at.apf.stexlife.parser.antlr4;
}

/**
 * Parser Rules
 */

program:	function* ;


function:	ID '(' paramList ')' '{' stmt* '}' ;
paramList:  (ID (',' ID)*)? ;

stmt:			(ifStmt|tryStmt|whileStmt|forStmt|foreachStm|throwStmt|returnStmt
					 |assignStmt ';'|declareStmt|voidFunctionCall);
					 
ifStmt:		'if' '(' expression ')' '{' stmt* '}' (elseIfStmt)* (elseBlock)? ;
elseIfStmt:         'elseif' '(' expression ')' '{' stmt* '}' ;
elseBlock:			'else' '{' stmt* '}' ;

tryStmt:		'try' '{' stmt* '}' catchBlock ;
catchBlock:			'catch' '(' ID ')' '{' stmt* '}' ;

whileStmt:		'while' '(' expression ')' '{' stmt* '}' ;

forStmt:        'for' '(' LET ID '=' expression ';' expression ';' assignStmt ')' '{' stmt* '}' ;
foreachStm:     'foreach' '(' LET ID 'in' expression ')' '{' stmt* '}' ;

throwStmt:		'throw' expression ';' ;
returnStmt:	    'return' (expression)? ';' ;

assignStmt: 	assignee '=' expression ;
declareStmt:		(LET|CONST) ID ('=' expression)? ';' ;

voidFunctionCall:		functionCall ';' ;

expression:      ('(' expression ')'|operation|operand|dynamicAccess|array|object|functionCall|anonymousFunction|functionRef) ;
operationExpression:	('(' expression ')'|operand|dynamicAccess|array|object|functionCall) ;

operand:		(identifier|value);
identifier:		ID ('.' ID)* ;
value:			(INT|FLOAT|BOOLEAN|NULL|STRING) ;
dynamicAccess:	identifier '[' expression ']' ;

object:				'{' (objectField (',' objectField)*)? '}';
objectField:		ID (':' expression)? ;

array:				'[' (expression (',' expression)*)? ']' ;

anonymousFunction:  '(' paramList ')' '{' stmt* '}' ;
functionRef:        ID '{' INT '}' ;

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