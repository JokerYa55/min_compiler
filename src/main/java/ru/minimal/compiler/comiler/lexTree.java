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
public class lexTree {

    private lexNode rootNode;

    public lexTree(token current) {
        rootNode = new lexNode();
        rootNode.setnToen(current);
        rootNode.setLevel(0);
    }

    public lexNode addNode(lexNode parent, token current) {
        lexNode res = new lexNode();
        res.setnToen(current);
        res.setLevel(parent.getLevel() + 1);
        if (parent.getlNode() != null) {
            parent.setrNode(res);
        } else {
            parent.setlNode(res);
        }
        res.setParrentNode(parent);
        //res.setLevel(parent.getLevel()+1);
        return res;
    }

    private String nodeToString(lexNode node, String par) {
        boolean flag = false;
        StringBuilder res = new StringBuilder();        
        res.append(par + " -> " + String.format("%"+(node.getnToen().toString().length() + 5*node.getLevel())+"s%n", node.getnToen().toString() + " - " + node.getLevel()));
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

}
