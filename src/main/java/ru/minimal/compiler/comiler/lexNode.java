/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.comiler;

import ru.minimal.compiler.interfaces.expInterface;
import ru.minimal.compiler.lexer.lexerConst;
import ru.minimal.compiler.lexer.lexerConst.expTypeEnum;
import ru.minimal.compiler.lexer.lexerConst.tokenEnum;
import ru.minimal.compiler.lexer.token;

/**
 *
 * @author vasl
 */
public class lexNode implements expInterface{

    private token nToen;
    // Левый операнд
    private lexNode lNode;
    // Правый операнд
    private lexNode rNode;
    // Дополнительный операнд для if
    private lexNode dNode;
    // Ссылка на родительский элемент
    private lexNode parrentNode;
    // Уровень в дереве
    private long level;
    // Значение перации
    private long valNode;
    // Тип узла OPER - Операция ????
    private tokenEnum lexNodeType;
    
    public token getnToen() {
        return nToen;
    }

    public void setnToken(token nToen) {
        this.nToen = nToen;
    }

    public lexNode getlNode() {
        return lNode;
    }

    public void setlNode(lexNode lNode) {
        this.lNode = lNode;
    }

    public lexNode getrNode() {
        return rNode;
    }

    public void setrNode(lexNode rNode) {
        this.rNode = rNode;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public lexNode getParrentNode() {
        return parrentNode;
    }

    public void setParrentNode(lexNode parrentNode) {
        this.parrentNode = parrentNode;
    }

    @Override
    public String toString() {
        return "lexNode{" + "nToen=" + nToen + '}';
    }

    @Override
    public expTypeEnum getType() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (lexerConst.isOper(nToen))
        {
            return lexerConst.expTypeEnum.OPER;
        }
        else
        {
            return lexerConst.expTypeEnum.BLOCK;
        }
    }

    @Override
    public void setType(lexerConst.expTypeEnum expType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //this.lexNodeType = 
    }

    public lexNode getdNode() {
        return dNode;
    }

    public void setdNode(lexNode dNode) {
        this.dNode = dNode;
    }

    public long getValNode() {
        return valNode;
    }

    public void setValNode(long valNode) {
        this.valNode = valNode;
    }
    
}
