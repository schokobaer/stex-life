grammar StexLifeGrammar;

@header {
	package at.apf.stexlife.parser.antlr4;
}

/**
 * Parser Rules
 */

program:	functionlist ;
functionlist:	function+ ;

function:	ID '(' paramlist ')' '{' stmt+ '}' ;
paramlist:  (ID (',' ID)*)? ;

stmt:			(ifStmt|tryStmt|whileStmt|throwStmt|returnStmt
					 |assignStmt|declareStmt|voidFunctionCall);
					 
ifStmt:		'if' '(' expression ')' '{' stmt+ '}' (elseIfStmt)* (elseBlock)? ;
elseIfStmt:         'elseif' '(' expression ')' '{' stmt+ '}' ;
elseBlock:			'else' '{' stmt+ '}' ;

tryStmt:		'try' '{' stmt+ '}' catchBlock ;
catchBlock:			'catch' '(' ID ')' '{' stmt+ '}' ;

whileStmt:		'while' '(' expression ')' '{' stmt+ '}' ;

throwStmt:		'throw' expression ';' ;
returnStmt:	    'return' (expression)? ';' ;

assignStmt: 	assignee '=' expression ';' ;
declareStmt:		(LET|CONST) ID ('=' expression)? ';' ;

voidFunctionCall:		functionCall ';' ;

expression:      ('(' expression ')'|operation|operand|arrayAccess|array|object|functionCall) ;
operationExpression:	('(' expression ')'|operand|arrayAccess|array|object|functionCall) ;

operand:		(identifier|value);
identifier:		ID ('.' ID)* ;
value:			(INT|FLOAT|BOOLEAN|NULL|STRING) ;
arrayAccess:	identifier '[' expression ']' ;

object:				'{' objectfield (',' objectfield)* '}';
objectfield:		ID (':' expression)? ;

array:				'[' (expression (',' expression)*)? ']' ;

functionCall:		ID '(' functionCallArgs ')' ;
functionCallArgs:	(expression (',' expression)*)? ;

operation:		    operationExpression operationtype expression
					| notoperation ;
notoperation:		'not' expression ;
operationtype:		('+'|'-'|'*'|'/'|'%'|'=='|'!='|'>'|'<'|'in'|'and'|'or') ;

assignee:			identifier | arrayAccess ;



/*
 * Lexer Rules
 */
WHITESPACE	:	[ \t\r\n]+ -> skip ;
COMMENT		:	'/*' .*? '*/' -> skip ;

INT         :   ([0-9])+ ;
FLOAT		:	([0-9])+ ('.' ([0-9])+ )? ;
BOOLEAN		:	('true' | 'false') ;
NULL		:	'null' ;
STRING		:	'"' (. | '\"')*? '"' ;

LET         : 'let' ;
CONST       : 'con' ;

ID			:	([A-Z]|[a-z])+([A-Z]|[a-z]|[0-9])* ;
