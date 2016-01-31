/*
 * Usercode Section
 */

 package com.aidanogrady.qgrady;

 import java_cup.runtime.*;


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
WhiteSpace     = {LineTerminator} | [ \t\f]
