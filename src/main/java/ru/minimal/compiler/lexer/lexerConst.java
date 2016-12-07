/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.lexer;

/**
 *
 * @author vasl
 */
public class lexerConst {

    public enum tokenEnum {
        NUM, ID, IF, ELSE, WHILE, DO, LBRA, RBRA, LPAR, RPAR, PLUS, MINUS, LESS, EQUAL, SEMICOLON, EOF
    };

    public enum expTypeEnum {
        OPER, BLOCK
    }

    public static boolean isOper(token tok) {
        boolean flag = false;
        switch (tok.getSym()) {
            case EQUAL:
                flag = true;
            case MINUS:
                flag = true;
            case PLUS:
                flag = true;
            default:
                break;
        }
        return flag;
    }
}
