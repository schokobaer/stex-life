grammar StexLifeGrammar;

@header {
	package at.apf.stexlife.parser.antlr4;
}

/**
 * Parser Rules
 */

program:	    (module)? include* function* ;

module:         'module' identifier ';' ;

include:        (FROM includeSource IMPORT includeDeclaration (',' includeDeclaration)*|IMPORT includeSource (alias)?) ';';
includeDeclaration: ID (alias)? ;
alias:          'as' ID ;
includeSource:  (ID|STRING) ;

function:       (EXPORT)? ID '(' paramList ')' block ;
paramList:      (ID (',' ID)*)? ;

stmt:           (block|ifStmt|tryStmt|whileStmt|forStmt|foreachStm|throwStmt|returnStmt
					 |assignStmt ';'|declareStmt|voidFunctionCall|BREAK ';'|CONTINUE ';');

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

assignStmt: 	assignee (shortAssignOp)?'=' expression ;
shortAssignOp:		(ADD|SUB|MUL|DIV|MOD) ;
declareStmt:		(LET|CONST) ID ('=' expression)? ';' ;

voidFunctionCall:		functionCall ';' ;

expression:      ('(' expression ')'|operation|operand|ternaryExpression|dynamicAccess|array|object|functionCall|anonymousFunction|selfFunctionRef) ;
operationExpression:	('(' expression ')'|operand|ternaryExpression|dynamicAccess|array|object|functionCall) ;

operand:		(identifier|value);
identifier:		(ID|SELF) ('.' ID)* ;
value:			(INT|FLOAT|BOOLEAN|NULL|STRING|elString|UNDEFINED) ;
elString:       '\'' (elExpression|.)*?  '\'' ;
elExpression:   '${' expression '}' ;
dynamicAccess:	identifier '[' expression ']' ;

object:				'{' (objectField (',' objectField)*)? '}';
objectField:		ID (':' expression)? ;

array:				'[' (expression (',' expression)*)? ']' ;

anonymousFunction:  '(' paramList ')' block ;

selfFunctionRef:    '::' expression ;

functionCall:		identifier '(' argList ')' ;
argList:            (expression (',' expression)*)? ;

operation:		    operationExpression operationType expression
					| notOperation ;
notOperation:		'not' expression ;
operationType:		(ADD|SUB|MUL|DIV|MOD|EQU|NEQ|GRT|GRE|SMT|SME|IN|AND|OR) ;

ternaryExpression:  'if' expression '?' expression ':' expression ;

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
UNDEFINED   :   'undefined' ;
STRING		:	'"' (. | '\"')*? '"' ;

LET         : 'let' ;
CONST       : 'con' ;
SELF        : 'this' ;
EXPORT      : 'export' ;
FROM        : 'from' ;
IMPORT      : 'import' ;
BREAK       : 'break' ;
CONTINUE    : 'continue' ;

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