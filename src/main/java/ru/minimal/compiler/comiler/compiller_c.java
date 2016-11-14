/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.comiler;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Stack;
import ru.minimal.compiler.lexer.lexerConst;
import ru.minimal.compiler.lexer.token;

/**
 *
 * @author vasl
 */
public class compiller_c {

    private lexTree tree;
    private lexNode currentNode;
    private Stack<lexNode> stack = new Stack();

    public compiller_c() {

    }

    // TODO: Нужно к корню добавить список exp и для каждого оператора делать свое дерево разбора
    
    public lexTree genlexTree(List<token> listToken) {
        if (listToken.size() > 0) {
            tree = new lexTree(listToken.get(0));

            this.currentNode = this.tree.getRootNode();
            token currentToken = null;
            for (int i = 1; i < listToken.size(); i++) {
                System.out.println("symvol = " + listToken.get(i));

                // Обрабатывае цифру
                if (listToken.get(i).getSym() == lexerConst.tokenEnum.NUM) {
                    System.out.println("Отрабатываем NUM");
                    lexNode node = new lexNode();
                    node.setnToen(listToken.get(i));
                    stack.push(node);
                    System.out.println("stack = " + stack.size());
                }

                // Обрабатываем конец операнда ;
                if (listToken.get(i).getSym() == lexerConst.tokenEnum.SEMICOLON) {
                    System.out.println("Отрабатываем ;");
                    //TODO: Дубируютс узлы при ввде операции + и идентификатора. Нужно предусотрет 
                    // универсалный инструент по добавлению узла (должен балансировать какую ветку сооздать) 
                    if (this.stack.size() == 1) {
                        if (this.currentNode.getrNode() == null) {
                            this.currentNode.setrNode(stack.pop());
                            this.currentNode.getrNode().setLevel(this.currentNode.getLevel() + 1);
                        } else {
                            this.currentNode.setlNode(stack.pop());
                            this.currentNode.getlNode().setLevel(this.currentNode.getLevel() + 1);
                        }
                        this.currentNode = this.tree.getRootNode();
                        System.out.println("stack = " + stack.size());
                    } else {
                        //lexNode temp;

                        this.currentNode.setrNode(stack.pop());
                        this.currentNode.getrNode().setLevel(this.currentNode.getLevel() + 1);
                        //this.currentNode = this.tree.getRootNode();

                        this.currentNode.setlNode(stack.pop());
                        this.currentNode.getlNode().setLevel(this.currentNode.getLevel() + 1);

                        this.currentNode = this.tree.getRootNode();
                        System.out.println("stack = " + stack.size());

                        System.out.println("stack = " + stack.size());

                    }
                    //currentToken = null;
                }

                // Обрабатываем переменную
                if (listToken.get(i).getSym() == lexerConst.tokenEnum.ID) // Если текущий токен идентификатор
                {
                    /*System.out.println("current token = " + currentToken);
                    if (currentToken == null) {*/
                    // если нет текущей операции то ддобавяе в стек
                    System.out.println("Отрабатываем ID");
                    lexNode node = new lexNode();
                    node.setnToen(listToken.get(i));
                    stack.push(node);
                    System.out.println("stack = " + stack.size());
                    //currentToken = listToken.get(i);
                    /* } else {
                        System.out.println("Отрабатываем ID1");
                        //Если есть текущая операция то добавляе узел справа
                        this.currentNode = this.tree.addNode(currentNode, listToken.get(i));
                        System.out.println("stack = " + stack.size());
                    }*/
                }

                //Обрабатываем операции
                switch (listToken.get(i).getSym()) {
                    case EQUAL:
                        System.out.println("Отрабатываем =");
                        this.currentNode = this.tree.addNode(currentNode, listToken.get(i));
                        this.currentNode.setlNode(stack.pop());
                        this.currentNode.getlNode().setLevel(this.currentNode.getLevel() + 1);
                        currentToken = listToken.get(i);
                        System.out.println("stack = " + stack.size());
                        break;
                    case IF:
                        this.currentNode = this.tree.addNode(currentNode, listToken.get(i));
                        break;
                    case PLUS:
                        this.currentNode = this.tree.addNode(currentNode, listToken.get(i));
                        this.currentNode.setlNode(stack.pop());
                        this.currentNode.getlNode().setLevel(this.currentNode.getLevel() + 1);
                        currentToken = listToken.get(i);
                        System.out.println("stack = " + stack.size());
                        break;
                    default:
                        //System.out.println("Не операция : " + listToken.get(i));
                        break;
                }

            }
        } else {
            System.out.println("Ошибка компилции");
        }
        return this.tree;
    }

    private void getNextNode(lexNode parentNode, FileWriter out) throws IOException {
        // Если операци то выполнем генерим код асм
        //System.out.println("ParentNode = " + parentNode);
        if (parentNode.getnToen().getSym() == lexerConst.tokenEnum.PLUS) {
            // обрабатываем "+"
            //System.out.println("Обрабатываем +");
            getNextNode(parentNode.getlNode(), out);
            getNextNode(parentNode.getrNode(), out);
            System.out.println("add;");
            out.write("add;\n");
        }

        if (parentNode.getnToen().getSym() == lexerConst.tokenEnum.EQUAL) {
            // обрабатываем "="
            //getNextNode(parentNode.getlNode());
            //System.out.println("брабатываем =");
            getNextNode(parentNode.getrNode(), out);
            System.out.println("store " + parentNode.getlNode().getnToen().getVal() + ";");
            out.write("store " + parentNode.getlNode().getnToen().getVal() + ";\n");
        }

        if (parentNode.getnToen().getSym() == lexerConst.tokenEnum.ID) {
            // обрабатываем ID     
            //System.out.println("брабатываем ID");
            System.out.println("push " + parentNode.getnToen().getVal() + ";");
            out.write("push " + parentNode.getnToen().getVal() + ";\n");
        }

        if (parentNode.getnToen().getSym() == lexerConst.tokenEnum.NUM) {
            // обрабатываем ID     
            //System.out.println("брабатываем NUM");
            System.out.println("push " + parentNode.getnToen().getVal() + ";");
            out.write("push " + parentNode.getnToen().getVal() + ";\n");
        }

        if (parentNode.getnToen().getSym() == lexerConst.tokenEnum.RBRA) {
            System.out.println("retr;");
            out.write("retr;\n");
        }
        
        if (parentNode.getnToen().getSym() == lexerConst.tokenEnum.LBRA) {
            // обрабатываем "{"
            //System.out.println("Обрабатываем {");

            if (parentNode.getlNode() != null) {
                getNextNode(parentNode.getlNode(), out);
            }

            if (parentNode.getrNode() != null) {
                getNextNode(parentNode.getrNode(), out);
                //System.out.println("add;");
            }
        }
    }

    public void genASText() throws IOException {

        lexNode currentNode = this.tree.getRootNode();
        // обходим дерево
        try (FileWriter wout = new FileWriter("/home/vasl/log/program.asm--")) {
            //Writer wout;
            getNextNode(currentNode, wout);
        }
    }

    private lexNode getNextToken() {
        return null;
    }

    public lexTree getTree() {
        return tree;
    }

}
