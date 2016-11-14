/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.comiler;

import ru.minimal.compiler.lexer.token;

/**
 *
 * @author vasl
 */
public class lexNode {

    private token nToen;
    private lexNode lNode;
    private lexNode rNode;
    private lexNode parrentNode;
    private long level;
    private long valNode; // Значение перации
    
    public token getnToen() {
        return nToen;
    }

    public void setnToen(token nToen) {
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
    
}
