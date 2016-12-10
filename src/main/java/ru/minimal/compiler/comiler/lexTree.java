/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.comiler;

import ru.minimal.compiler.interfaces.expInterface;
import ru.minimal.compiler.lexer.lexerConst;
import ru.minimal.compiler.lexer.token;

/**
 *
 * @author vasl
 */
public class lexTree implements expInterface {

    private lexNode rootNode;
    private lexerConst.expTypeEnum expType;

    public lexTree(token current) {
        rootNode = new lexNode();
        rootNode.setnToken(current);
        rootNode.setLevel(0);
    }

    public lexTree() {

    }

    public lexTree(lexNode rNode) {
        this.rootNode = rNode;
    }

    public lexNode addNode(lexNode parent, token current) {
        lexNode res = new lexNode();
        res.setnToken(current);

        if (this.rootNode == null) {
            rootNode = res;
            res.setParrentNode(null);
            res.setlNode(null);
            res.setLevel(0);
        } else {
            res.setLevel(parent.getLevel() + 1);
            if (parent.getlNode() != null) {
                parent.setrNode(res);
            } else {
                parent.setlNode(res);
            }
            res.setParrentNode(parent);
        }
        //res.setLevel(parent.getLevel()+1);
        return res;
    }

    private String nodeToString(lexNode node, String par) {
        boolean flag = false;
        StringBuilder res = new StringBuilder();
        res.append(par + "\t-> " + String.format("%" + (node.getnToen().toString().length() + 5 * node.getLevel()) + "s%n", node.getnToen().toString() + " - " + node.getLevel()));
        if (node.getlNode() != null) {
            res.append(nodeToString(node.getlNode(), "left"));
        }

        if (node.getrNode() != null) {
            res.append(nodeToString(node.getrNode(), "right"));
        }
        return res.toString();
    }

    @Override
    public String toString() {
        return nodeToString(rootNode, "root");
    }

    public lexNode getRootNode() {
        return rootNode;
    }

    @Override
    public lexerConst.expTypeEnum getType() {
        return this.expType;
    }

    @Override
    public void setType(lexerConst.expTypeEnum expType) {
        this.expType = expType;
    }

}
