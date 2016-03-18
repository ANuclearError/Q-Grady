/*
 * Usercode Section
 */

package com.aidanogrady.qgrady.syntax;

import java_cup.runtime.*;


%%

/******************************************************************************/

/*
 * Options/Declarations
 */

/* The name of the class JFlex will create. */
%class Lexer

/* The current line and column. */
%line
%column

/* CUP compatibility mode */
%cup

/* Make public */
%public

/* Declarations */
%{

    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

/* Macro Declarations */
LineTerminator = \r|\n|\r\n
Space     = {LineTerminator} | [ \t\f]
dec = ([0-9]*\.)?[0-9]+
str = [a-zA-Z$_] [a-zA-Z0-9$_]*

%%

/******************************************************************************/

/*
 * Lexical Rules
 */

<YYINITIAL> {

    /* Return the token SEMI declared in the class sym that was found. */

    /* Print the token found that was declared in the class sym and then
       return it.*/
    "->"    { return symbol(sym.ARROW);     }
    ","     { return symbol(sym.COMMA);     }
    "["     { return symbol(sym.LBRACKET);  }
    "]"     { return symbol(sym.RBRACKET);  }
    ";"     { return symbol(sym.SEMICOLON); }


    /* If an integer is found print it out, return the token NUMBER
       that represents an integer and the value of the integer that is
       held in the string yytext which will get turned into an integer
       before returning */
    {dec}   { return symbol(sym.NUM, new Double(yytext())); }
    {str}   { return symbol(sym.VAR, yytext()); }

    /* Don't do anything if whitespace is found */
    {Space} { /* just skip what was found, do nothing */ }
}


/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^]         { throw new Error("Illegal character <"+yytext()+">"); }
