/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.comiler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.log4j.Logger;
import ru.minimal.compiler.interfaces.compilerInterface;
import ru.minimal.compiler.interfaces.expInterface;
import ru.minimal.compiler.lexer.lexerConst;
import ru.minimal.compiler.lexer.token;

/**
 *
 * @author vasl
 */
public class compiller_exp implements compilerInterface {

    static Logger log = Logger.getLogger(compiller_exp.class);
    private lexTree tree;
    private lexNode currentNode;
    private programBlock currentProgramBlock;
    private int currentBlockNum = 0;
    // стек для операндв
    private Stack<lexNode> stack = new Stack();
    // стек для программных бллоков
    private Stack<programBlock> blockStack = new Stack();
    // Стек для скобок
    private Stack<String> stackSc = new Stack<>();
    // Стек для выражений
    private StackExp stackExp = new StackExp();
    /*
        programStruct   - структура программы. состоит из блоков
        programBlock    - блок операторов объедененных логически. Всегда есть 
                          осноовной блок содержащий все операторы состоит из 
                          списка операторов (синтаксических деревьев разбора оператора)
        operTreeList    - дерево синтаксического разбора оператора
     */

    private programStract ps;

    public compiller_exp() {

    }

    // Создаем нвый блок
    private programBlock addProgBlock() {
        log.debug("addProgBlock()");
        //programBlock pbTemp = null;
        try {
            log.debug("Создаем нвый блок");
            this.stackSc.push("{");
            // Сохранем старый блок в стеке
            this.blockStack.push(currentProgramBlock);
            this.currentBlockNum++;
            // Создаем нвый блок и делаем ег текущим
            this.currentProgramBlock = this.ps.newPrograBlock(currentProgramBlock, "main_" + currentBlockNum);
        } catch (Exception e) {
            log.error(e);
        }
        return this.currentProgramBlock;
    }

    // выходим из блока и возвращаем вышестоящий блок
    private programBlock endBlock() {
        log.debug("endBlock()");
        try {
            // обрабатываем конец нового блока            
            this.stackSc.pop();
            log.debug("обрабатываем конец текущего блока блока");
            this.currentProgramBlock = blockStack.pop();
        } catch (Exception e) {
            log.error(e);
        }
        return this.currentProgramBlock;
    }

    // TODO: Нужно к корню добавить список exp и для каждого оператора делать свое дерево разбора
    @Override
    public programStract genlexTree(List<token> listToken) {
        try {
            log.debug("genlexTree");
            if (listToken.size() > 0) {
                // Проверяем первый символ
                if (listToken.get(0).getSym() == lexerConst.tokenEnum.LBRA) {
                    // Создаем основной бллок и структуру программы
                    this.ps = new programStract();
                    // Устанавливаем текущий блок
                    this.currentProgramBlock = ps.getPblock();
                    blockStack.push(currentProgramBlock);
                    //              blockStack.push(currentProgramBlock);
                    // Добавляем "{" в стек
                    this.stackSc.add("{");

                    tree = null;

                    //tree = new lexTree(listToken.get(0));
                    //this.currentNode = this.tree.getRootNode();
                    token currentToken = null;
                    List<token> exp = new ArrayList<>();
                    int i = 0;
                    //for (int i = 1; i < listToken.size(); i++) {
                    while (i < listToken.size()) {
                        log.debug("Обрабатываем токен : " + listToken.get(i));
                        // Обрабатываем новый блок
                        if (listToken.get(i).getSym() == lexerConst.tokenEnum.LBRA) {
                            // Создаем нвый блок
                            /*log.debug("Создаем нвый блок");
                            this.stackSc.push("{");
                            // Сохранем старый блок в стеке
                            this.blockStack.push(currentProgramBlock);
                            this.currentBlockNum++;
                            // Создаем нвый блок и делаем ег текущим
                            this.currentProgramBlock = this.ps.newPrograBlock(currentProgramBlock, "main_" + currentBlockNum);
                            //this.currentProgramBlock.addOper(this.currentProgramBlock);*/
                            addProgBlock();
                            i++;
                        } else if (listToken.get(i).getSym() == lexerConst.tokenEnum.RBRA) {
                            // обрабатываем конец нового блока
                            log.debug("Обрабатываем конец нового блока : " + listToken.get(i).getSym());
                            /*this.stackSc.pop();
                            log.debug("обрабатываем конец нового блока");
                            this.currentProgramBlock = blockStack.pop();*/
                            endBlock();
                            i++;
                        } else {
                            // Выделяем выражения
                            // Обрабатываем конец операнда ;
                            exp.add(listToken.get(i));
                            if (listToken.get(i).getSym() == lexerConst.tokenEnum.SEMICOLON) {
                                log.debug("Отрабатываем ;");
                                // Проверяем является ли следующий символ символом }                            
                                lexNode cNode = getExp(exp.subList(0, exp.size() - 1), null);
                                lexTree lTree = new lexTree(cNode);
                                this.currentProgramBlock.addOper(lTree);
                                exp.clear();
                            }
                            i++;
                        }
                    }
                }
            } else {
                log.error("Ошибка компилции");
                throw new Exception("Ошибка компилции");
            }
        } catch (Exception e) {
            log.error("Ошибка : " + e.getMessage());
        }
        return this.ps;
    }

    // Обрабатываем скобки в выражениях. Возвращает позицию закрывающей скобки для выражения
    private int getParEndPost(List<token> exp) {
        int res = 0;
        // Стек для скобок
        Stack<token> st = new Stack<>();
        int i = 0;
        do {
            if (exp.get(i).getSym() == lexerConst.tokenEnum.LPAR) {
                st.push(exp.get(i));
            }

            if (exp.get(i).getSym() == lexerConst.tokenEnum.RPAR) {
                st.pop();
            }
            res = i;
            i++;
        } while (st.size() > 0);

        return res;
    }

    // Разбирает выражения
    private lexNode getExp(List<token> exp, lexNode parentNode) {
        //int b;
        //int a=(((7+3)+(b+5))+b+(b+7))+b;
        log.debug("exp = " + exp);
        lexNode localNode = new lexNode();
        int len = exp.size() - 1;
        if (len == 0) {
            // если мы получаем переменную либо число создаем узел
            log.debug("Обрабатываем идентификатор или число : " + exp.toString());
            localNode.setnToken(exp.get(len));
            localNode.setLevel(parentNode.getLevel() + 1);
        } else {
            log.debug("Обрабатываем выражение : " + exp.toString());
            // Если выражение
            // Левое выражение
            List<token> lExp = new ArrayList<>();
            // Правое выражение
            List<token> rExp = new ArrayList<>();
            // Выражение для else
            List<token> elseExp = new ArrayList<>();
            int i = 0;
            if (null == exp.get(0).getSym()) {
                log.debug("Обработка операции типа a+b");
                boolean flag = true;
                while (flag) {
                    if (lexerConst.isOper(exp.get(i))) {
                        flag = false;
                        // Если операция получаем левое и правое выражения и обрабатываем их
                        log.error("Обработка операции : " + exp.get(i));

                        // Проверяем на скобки
                        lExp = exp.subList(0, i);
                        rExp = exp.subList(i + 1, len + 1);

                        // Вычисляем уровень 
                        long nLevel = 0;
                        if (parentNode != null) {
                            nLevel = parentNode.getLevel() + 1;
                        }
                        // Устанавливаем уровень
                        localNode.setLevel(nLevel);
                        // Устанавливаем значение узла
                        localNode.setnToken(exp.get(i));
                        // Устанавливаем тип узла на OPER
                        //localNode.setType(lexerConst.expTypeEnum.OPER);

                        // Вычисляем левое и правое выражения
                        if (lExp != null) {
                            localNode.setlNode(getExp(lExp, localNode));
                        }
                        if (rExp != null) {
                            localNode.setrNode(getExp(rExp, localNode));
                        }
                    } else {
                        i++;
                    }
                }
            } else // обрабатываем if, for, while
            {
                switch (exp.get(0).getSym()) {
                    case IF: {
                        log.debug("Обрабатываем : " + exp.get(0).getSym());

                        if (parentNode != null) {
                            localNode.setLevel(parentNode.getLevel() + 1);
                        } else {
                            localNode.setLevel(0);
                        }
                        localNode.setnToken(exp.get(0));

                        // Разбиваем  IF на составляющие
                        List<token> logicalExp = new ArrayList<>();
                        token cToken;
                        i++;
                        // Выделяем выражение условия и записываем в левый операнд и записываем в правый блок 
                        while (exp.get(i).getSym() != lexerConst.tokenEnum.RPAR) {
                            cToken = exp.get(i);
                            if (cToken.getSym() != lexerConst.tokenEnum.LPAR) {
                                logicalExp.add(exp.get(i));
                            }
                            i++;
                        }
                        localNode.setlNode(getExp(logicalExp, localNode));
                        // Выделяем блок операндов если условие TRUE
                        while (exp.get(i).getSym() != lexerConst.tokenEnum.RBRA) {
                            cToken = exp.get(i);
                            if (cToken.getSym() != lexerConst.tokenEnum.LPAR) {
                                logicalExp.add(exp.get(i));
                            }
                            i++;
                        }
                        // Выделяем выражение для исполнения ELSE если есть и записываем в блок else 
                        // Если есть е
                        int p = getParEndPost(exp);
                        lExp = exp.subList(1, p);
                        break;
                    }
                    case LPAR: {
                        // обрабатываем скобку
                        log.debug("Обрабатываем скобку");
                        int p = getParEndPost(exp);
                        if (p < len) {
                            // Если выражение типа (exp) <oper> exp
                            log.debug("Обрабатываем выражение типа (exp) <oper> exp");
                            lExp = exp.subList(1, p);
                            rExp = exp.subList(p + 2, len + 1);
                            if (parentNode != null) {
                                localNode.setLevel(parentNode.getLevel() + 1);
                            } else {
                                localNode.setLevel(0);
                            }

                            localNode.setnToken(exp.get(p + 1));
                            // Вычисляем левое и правое выражения
                            if (lExp != null) {
                                localNode.setlNode(getExp(lExp, localNode));
                            }
                            if (rExp != null) {
                                localNode.setrNode(getExp(rExp, localNode));
                            }

                        } else {
                            // Если выражение (exp)
                            log.debug("Обрабатываем выражение типа (exp)");
                            List<token> expTemp = exp.subList(1, len);
                            localNode = getExp(expTemp, parentNode);
                        }
                        break;
                    }
                    default:
                        log.debug("Обработка операции типа a+b");
                        boolean flag = true;
                        while (flag) {
                            if (lexerConst.isOper(exp.get(i))) {
                                flag = false;
                                // Если операция получаем левое и правое выражения и обрабатываем их
                                log.error("Обработка операции : " + exp.get(i));

                                // Проверяем на скобки
                                lExp = exp.subList(0, i);
                                rExp = exp.subList(i + 1, len + 1);

                                // Вычисляем уровень
                                long nLevel = 0;
                                if (parentNode != null) {
                                    nLevel = parentNode.getLevel() + 1;
                                }
                                // Устанавливаем уровень
                                localNode.setLevel(nLevel);
                                // Устанавливаем значение узла
                                localNode.setnToken(exp.get(i));
                                // Устанавливаем тип узла на OPER
                                //localNode.setType(lexerConst.expTypeEnum.OPER);

                                // Вычисляем левое и правое выражения
                                if (lExp != null) {
                                    localNode.setlNode(getExp(lExp, localNode));
                                }
                                if (rExp != null) {
                                    localNode.setrNode(getExp(rExp, localNode));
                                }
                            } else {
                                i++;
                            }
                        }
                        break;
                }
            }
        }
        return localNode;
    }

    private void getNextNode(lexNode parentNode, FileWriter out) throws IOException {
        // Если операци то выполнем генерим код асм
        log.debug("getNextNode: Если операци то выполнем генерим код асм");
        log.debug("ParentNode = " + parentNode);
        if (parentNode.getnToen().getSym() == lexerConst.tokenEnum.PLUS) {
            // обрабатываем "+"
            log.debug("Обрабатываем +");
            getNextNode(parentNode.getlNode(), out);
            getNextNode(parentNode.getrNode(), out);
            log.debug("add;");
            out.write("add;\n");
        }

        if (parentNode.getnToen().getSym() == lexerConst.tokenEnum.MINUS) {
            // обрабатываем "-"
            log.debug("Обрабатываем +");
            getNextNode(parentNode.getlNode(), out);
            getNextNode(parentNode.getrNode(), out);
            log.debug("sub;");
            out.write("sub;\n");
        }

        if (parentNode.getnToen().getSym() == lexerConst.tokenEnum.MUL) {
            // обрабатываем "-"
            log.debug("Обрабатываем *");
            getNextNode(parentNode.getlNode(), out);
            getNextNode(parentNode.getrNode(), out);
            log.debug("mul;");
            out.write("mul;\n");
        }

        if (parentNode.getnToen().getSym() == lexerConst.tokenEnum.EQUAL) {
            // обрабатываем "="
            //getNextNode(parentNode.getlNode());
            log.debug("брабатываем =");
            getNextNode(parentNode.getrNode(), out);
            log.debug("store " + parentNode.getlNode().getnToen().getVal() + ";");
            out.write("store " + parentNode.getlNode().getnToen().getVal() + ";\n");
        }

        if (parentNode.getnToen().getSym() == lexerConst.tokenEnum.ID) {
            // обрабатываем ID     
            log.debug("обрабатываем ID");
            log.debug("push " + parentNode.getnToen().getVal() + ";");
            out.write("push " + parentNode.getnToen().getVal() + ";\n");
        }

        if (parentNode.getnToen().getSym() == lexerConst.tokenEnum.NUM) {
            // обрабатываем ID     
            //System.out.println("брабатываем NUM");
            //      System.out.println("push " + parentNode.getnToen().getVal() + ";");
            out.write("push " + parentNode.getnToen().getVal() + ";\n");
        }

        if (parentNode.getnToen().getSym() == lexerConst.tokenEnum.RBRA) {
            //    System.out.println("retr;");
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
                log.debug("add;");
            }
        }
    }

    public void genBlokASMText(programBlock block, FileWriter out) throws IOException {
        try {
            log.debug("Обработка блока " + block.getBlockName());
            // Получаем список операций в блоке 
            for (expInterface item : block.getOperList()) {
                if (item.getType() == lexerConst.expTypeEnum.BLOCK) {
                    programBlock pb = (programBlock) item;

                   /* for (expInterface item1 : pb.getOperList()) {
                        getNextNode(((lexTree) item1).getRootNode(), out);
                    }*/
                    genBlokASMText(pb, out);
                } else {
                    getNextNode(((lexTree) item).getRootNode(), out);
                }

            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    /*public void genBlokASMText(programBlock block, FileWriter out) throws IOException {
        log.debug("Обработка блока " + block.getBlockName());
        for (expInterface item : block.getOperList()) {
            System.out.println("type = " + item.getType());
            if (lexerConst.expTypeEnum.OPER == item.getType()) {
                // обрабатываем операцию
                log.debug("Обрабатывается операция");
                log.debug(((lexTree) item).toString());
                getNextNode(((lexTree) item).getRootNode(), out);
            } else {
                //Обрабатываем вложенный блок кода
                // Добавлем метку в ASM код
                log.debug("*******************" + ((programBlock) item).getBlockName() + "********************");
                log.debug("Метка = " + ((programBlock) item).getBlockName());
                out.write(((programBlock) item).getBlockName() + ":\n");
                genBlokASMText(((programBlock) item), out);
            }
        }       
    }*/
    @Override
    public void genASText(String outFile) throws IOException {
        // обходим дерево
        log.debug("Генерация кода ASM");
        try (FileWriter wout = new FileWriter(outFile)) {

            genBlokASMText(this.ps.getPblock(), wout);
        }
    }

    private lexNode getNextToken() {
        return null;
    }

    public lexTree getTree() {
        return tree;
    }

}
