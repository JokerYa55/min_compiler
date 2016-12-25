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
import ru.minimal.compiler.lexer.lexerConst;
import ru.minimal.compiler.lexer.token;
import ru.minimal.compiler.interfaces.pNodeInterface;

/**
 *
 * @author vasl
 */
public class compiller_exp implements compilerInterface {

    static Logger log = Logger.getLogger(compiller_exp.class);
    private lexTree tree;
    private lexNode currentNode;
    private programBlock currentProgramBlock;
    private int labelCount = 0;
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
    private programBlock addProgBlock(programBlock parentBlock) {
        log.debug("addProgBlock()");
        //programBlock pbTemp = null;
        programBlock res = null;
        try {
            log.debug("Создаем нвый блок");
            this.stackSc.push("{");
            // Сохранем старый блок в стеке
            //this.blockStack.push(currentProgramBlock);
            this.currentBlockNum++;
            // Создаем нвый блок и делаем ег текущим
            res = this.ps.newPrograBlock(parentBlock, "main_" + this.currentBlockNum);
        } catch (Exception e) {
            log.error(e);
        }
        return res;
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

    // TODO: Обработка блока
    public programBlock getPB(pNodeInterface parrentBlock, List<token> listToken) {
        programBlock res = null;
        try {
            log.debug("--------------------------------- genPB ---------------------------------");
            log.debug("PB=>" + listToken);
            //log.debug("parentBlock => " + parrentBlock);
            // Добавляем новый блок
            if (parrentBlock == null) {
                log.debug("Добавляем блок => main.");
                // Создаем основной бллок и структуру программы
                this.ps = new programStract();
                // Устанавливаем текущий блок
                res = ps.getPblock();                   
                //blockStack.push(currentProgramBlock);
            } else {
                log.debug("Добавляем новый блок.");
                res = addProgBlock((programBlock) parrentBlock);
            }
            // Обрабатываем поток лексем
            int i = 0;
            List<token> tempPB = new ArrayList();
            List<token> exp = new ArrayList<>();
            while (i < listToken.size()) {
                log.debug("Обрабатываем токен : " + listToken.get(i));
                if (listToken.get(i).getSym() == lexerConst.tokenEnum.LBRA) {
                    log.debug("Обрабатываем новый блок");
                    // Выбираем выражения
                    stackSc.clear();
                    stackSc.add("{");
                    // Выделяем блок "{}"
                    while (stackSc.size() != 0) {
                        if (listToken.get(i).getSym() == lexerConst.tokenEnum.LBRA) {
                            stackSc.add("{");
                        } else if (listToken.get(i).getSym() == lexerConst.tokenEnum.RBRA) {
                            stack.pop();
                        } else {
                            tempPB.add(listToken.get(i));
                        }
                        i++;
                    }
                    res = getPB(res, tempPB);
                } else if (listToken.get(i).getSym() == lexerConst.tokenEnum.WHILE) {
                    // Обрабатываем операторы WHILE
                    log.debug("Обрабатываем : " + listToken.get(i).getSym());
                    exp.clear();
                    i--;
                    do {
                        i++;
                        exp.add(listToken.get(i));
                    } while (listToken.get(i).getSym() != lexerConst.tokenEnum.RBRA);

                    lexNode cNode = (lexNode) getExp(exp.subList(0, exp.size()), null);
                    lexTree lTree = new lexTree(cNode);
                    res.addOper(lTree);
                    exp.clear();
                    i++;

                } else if (listToken.get(i).getSym() == lexerConst.tokenEnum.IF) {
                    // Обрабатываем операторы IF
                    log.debug("Обрабатываем : " + listToken.get(i).getSym());
                    // Выделяем весь оператор IF и передаем в getExp
                    exp.clear();
                    i--;
                    do {
                        i++;
                        exp.add(listToken.get(i));
                    } while (listToken.get(i).getSym() != lexerConst.tokenEnum.RBRA);                    
                    
                    i++;
                    if (listToken.get(i).getSym() == lexerConst.tokenEnum.ELSE) {
                        log.debug("Добавляем ELSE");
                        i--;
                        do {
                            i++;
                            exp.add(listToken.get(i));
                        } while (listToken.get(i).getSym() != lexerConst.tokenEnum.RBRA);
                    }

                    log.debug("Обработка оператора IF = " + exp + "\n");
                    
                    lexNode cNode = (lexNode) getExp(exp.subList(0, exp.size()), res);
                    lexTree lTree = new lexTree(cNode);
                    //this.currentProgramBlock.addOper(lTree);
                    res.addOper(lTree);
                    log.debug("Конец обработки оператора IF\n");
                    exp.clear();
                } else {
                    // Выделяем выражения
                    // Обрабатываем конец операнда ;
                    exp.add(listToken.get(i));
                    if (listToken.get(i).getSym() == lexerConst.tokenEnum.SEMICOLON) {
                        log.debug("Отрабатываем ; \n");
                        lexNode cNode = (lexNode) getExp(exp.subList(0, exp.size() - 1), null);
                        lexTree lTree = new lexTree(cNode);
                        res.addOper(lTree);
                        log.debug("Конец обработки ; \n");                        
                        exp.clear();
                    }
                    i++;
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return res;
    }

    // TODO: Нужно к корню добавить список exp и для каждого оператора делать свое дерево разбора
   /* public programStract genlexTree(List<token> listToken, programBlock currentBlock) {
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
                            addProgBlock(currentBlock);
                            i++;
                        } else if (listToken.get(i).getSym() == lexerConst.tokenEnum.RBRA) {
                            // обрабатываем конец нового блока
                            log.debug("Обрабатываем конец нового блока : " + listToken.get(i).getSym());
                            endBlock();
                            i++;

                        } //
                        else if (listToken.get(i).getSym() == lexerConst.tokenEnum.FOR) {
                            // обрабатываем FOR
                            log.debug("Обрабатываем : " + listToken.get(i).getSym());
                            exp.clear();
                            i--;
                            do {
                                i++;
                                exp.add(listToken.get(i));
                            } while (listToken.get(i).getSym() != lexerConst.tokenEnum.RBRA);

                            lexNode cNode = (lexNode) getExp(exp.subList(0, exp.size()), null);
                            lexTree lTree = new lexTree(cNode);
                            this.currentProgramBlock.addOper(lTree);
                            exp.clear();
                            i++;

                        } else if (listToken.get(i).getSym() == lexerConst.tokenEnum.WHILE) {
                            // Обрабатываем операторы WHILE
                            log.debug("Обрабатываем : " + listToken.get(i).getSym());
                            exp.clear();
                            i--;
                            do {
                                i++;
                                exp.add(listToken.get(i));
                            } while (listToken.get(i).getSym() != lexerConst.tokenEnum.RBRA);

                            lexNode cNode = (lexNode) getExp(exp.subList(0, exp.size()), null);
                            lexTree lTree = new lexTree(cNode);
                            this.currentProgramBlock.addOper(lTree);
                            exp.clear();

                            i++;

                        } else if (listToken.get(i).getSym() == lexerConst.tokenEnum.IF) {
                            // Обрабатываем операторы IF
                            log.debug("Обрабатываем : " + listToken.get(i).getSym());
                            // Выделяем весь оператор IF и передаем в getExp
                            exp.clear();
                            i--;
                            do {
                                i++;
                                exp.add(listToken.get(i));
                            } while (listToken.get(i).getSym() != lexerConst.tokenEnum.RBRA);

                            i++;
                            if (listToken.get(i).getSym() == lexerConst.tokenEnum.ELSE) {
                                log.debug("Добавляем ELSE");
                                i--;
                                do {
                                    i++;
                                    exp.add(listToken.get(i));
                                } while (listToken.get(i).getSym() != lexerConst.tokenEnum.RBRA);
                            }

                            lexNode cNode = (lexNode) getExp(exp.subList(0, exp.size()), null);
                            lexTree lTree = new lexTree(cNode);
                            this.currentProgramBlock.addOper(lTree);
                            exp.clear();
                        } else {
                            // Выделяем выражения
                            // Обрабатываем конец операнда ;
                            exp.add(listToken.get(i));
                            if (listToken.get(i).getSym() == lexerConst.tokenEnum.SEMICOLON) {
                                log.debug("Отрабатываем ;");
                                lexNode cNode = (lexNode) getExp(exp.subList(0, exp.size() - 1), null);
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
    }*/

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
    private pNodeInterface getExp(List<token> exp, pNodeInterface parrentNode) {
        //int b;
        //int a=(((7+3)+(b+5))+b+(b+7))+b;
        log.debug("exp = " + exp);
        lexNode localNode = new lexNode();
        int len = exp.size() - 1;
        if (len == 0) {
            // если мы получаем переменную либо число создаем узел
            log.debug("Обрабатываем идентификатор или число : " + exp.toString());
            localNode.setnToken(exp.get(len));
            localNode.setLevel(parrentNode.getLevel() + 1);
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
                        if (parrentNode != null) {
                            nLevel = parrentNode.getLevel() + 1;
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
                log.debug("sym = " + exp.get(0).getSym());
                switch (exp.get(0).getSym()) {
                    case IF: {
                        try {
                            log.debug("Обрабатываем IF");
                            // Создаем узел для IF
                            if (parrentNode != null) {
                                localNode.setLevel(parrentNode.getLevel() + 1);
                            } else {
                                localNode.setLevel(0);
                            }
                            localNode.setnToken(exp.get(0));
                            // Получаем блок команд для всех ветвей IF 
                            // Разбиваем  IF на составляющие

                            token cToken;
                            i++;
                            // Выделяем выражение условия и записываем в левый операнд и записываем в правый блок 
                            while (exp.get(i).getSym() != lexerConst.tokenEnum.RPAR) {
                                cToken = exp.get(i);
                                if (cToken.getSym() != lexerConst.tokenEnum.LPAR) {
                                    lExp.add(cToken);
                                }
                                i++;
                            }
                            log.debug("\tУсловие IF = " + lExp);
                            // Выделяем блок операндов если условие TRUE//                           
                            i++;
                            stackSc.clear();
                            do {
                                log.debug(exp.get(i).getSym());
                                if (exp.get(i).getSym() == lexerConst.tokenEnum.LBRA) {
                                    stackSc.add("{");
                                } else if (exp.get(i).getSym() == lexerConst.tokenEnum.RBRA) {
                                    stackSc.pop();
                                } else {
                                    rExp.add(exp.get(i));
                                }
                                i++;
                            } while (stackSc.size() > 0);
                            log.debug("\tБлок TRUE IF = " + rExp);

                            log.debug("\tОбработка блока TRUE");
                            programBlock pb1 = getPB((programBlock) parrentNode, rExp);
                            //log.debug("\tБлок TRUE = " + pb1 + "\n");
                            // Выделяем ELSE
                            i = i + 2;
                            while (exp.get(i).getSym() != lexerConst.tokenEnum.RBRA) {
                                cToken = exp.get(i);
                                if (cToken.getSym() == lexerConst.tokenEnum.RBRA) {
                                    break;
                                } else if ((cToken.getSym() != lexerConst.tokenEnum.LBRA) && (cToken.getSym() != lexerConst.tokenEnum.RBRA) && (cToken.getSym() != lexerConst.tokenEnum.SEMICOLON)) {
                                    elseExp.add(cToken);
                                }
                                i++;
                            }
                            
                            log.debug("\tОбработка блока FALSE");
                            programBlock pb2 = getPB((programBlock) parrentNode, rExp);
                            //log.debug("\tБлок TRUE = " + pb1 + "\n");
                            
                            log.debug("\tБлок FALSE IF = " + rExp);
                            
                            localNode.setlNode(getExp(lExp, localNode));
                            localNode.setrNode(pb1);
                            localNode.setdNode(pb2);                       
                            break;
                        } catch (Exception e) {
                            log.error(e);
                            break;
                        }
                    }

                    case FOR: {
                        // обрабатываем FOR
                        log.debug("Обрабатываем FOR");
                        try {
                            // Создаем узел для FOR
                            if (parrentNode != null) {
                                localNode.setLevel(parrentNode.getLevel() + 1);
                            } else {
                                localNode.setLevel(0);
                            }
                            localNode.setnToken(exp.get(0));
                            token cToken;
                            i++;
                            // Разделем выражени в скбках на части 1- переменна цикла; 2 - Условие кончание цикла; пераци с переменнй цикла

                            // Выделяем выражение в скбках инициаци переменнй 
                            while (exp.get(i).getSym() != lexerConst.tokenEnum.SEMICOLON) {
                                cToken = exp.get(i);
                                if (cToken.getSym() != lexerConst.tokenEnum.LPAR) {
                                    lExp.add(cToken);
                                }
                                i++;
                            }
                            log.debug("lExp = " + lExp);
                            i++;
                            // Выделяем выражение услвие цикла 
                            while (exp.get(i).getSym() != lexerConst.tokenEnum.SEMICOLON) {
                                cToken = exp.get(i);
                                if (cToken.getSym() != lexerConst.tokenEnum.LPAR) {
                                    elseExp.add(cToken);
                                }
                                i++;
                            }
                            log.debug("elseExp = " + elseExp);
                            i++;
                            // Выделяем выражение в скбках изменение переменнй 
                            while (exp.get(i).getSym() != lexerConst.tokenEnum.SEMICOLON) {
                                cToken = exp.get(i);
                                if (cToken.getSym() != lexerConst.tokenEnum.LPAR) {
                                    rExp.add(cToken);
                                }
                                i++;
                            }
                            log.debug("rExp = " + rExp);

                            rExp.clear();

                            i = i + 2;
                            // Выделяем блок операндов в {}
                            while (exp.get(i).getSym() != lexerConst.tokenEnum.RBRA) {
                                cToken = exp.get(i);
                                if (cToken.getSym() == lexerConst.tokenEnum.RBRA) {
                                    i++;
                                    break;
                                } else if ((cToken.getSym() != lexerConst.tokenEnum.LBRA) && (cToken.getSym() != lexerConst.tokenEnum.RBRA) && (cToken.getSym() != lexerConst.tokenEnum.SEMICOLON)) {
                                    rExp.add(cToken);
                                    i++;
                                } else {
                                    i++;
                                }
                            }

                            localNode.setlNode(getExp(lExp, localNode));
                            localNode.setdNode(getExp(elseExp, localNode));
                            localNode.setrNode(getExp(rExp, localNode));
                        } catch (Exception e) {
                            log.error(e);
                        }

                        break;
                    }

                    case WHILE: {
                        // обрабатываем WHILE
                        log.debug("Обрабатываем WHILE");
                        try {
                            // Создаем узел для WHILE
                            if (parrentNode != null) {
                                localNode.setLevel(parrentNode.getLevel() + 1);
                            } else {
                                localNode.setLevel(0);
                            }
                            localNode.setnToken(exp.get(0));
                            token cToken;
                            i++;
                            // Выделяем выражение условия и записываем в левый операнд и записываем в правый блок 
                            while (exp.get(i).getSym() != lexerConst.tokenEnum.RPAR) {
                                cToken = exp.get(i);
                                if (cToken.getSym() != lexerConst.tokenEnum.LPAR) {
                                    lExp.add(cToken);
                                }
                                i++;
                            }
                            i++;
                            // Выделяем блок операндов в {}
                            while (exp.get(i).getSym() != lexerConst.tokenEnum.RBRA) {
                                cToken = exp.get(i);
                                if (cToken.getSym() == lexerConst.tokenEnum.RBRA) {
                                    i++;
                                    break;
                                } else if ((cToken.getSym() != lexerConst.tokenEnum.LBRA) && (cToken.getSym() != lexerConst.tokenEnum.RBRA) && (cToken.getSym() != lexerConst.tokenEnum.SEMICOLON)) {
                                    rExp.add(cToken);
                                    i++;
                                } else {
                                    i++;
                                }
                            }

                            localNode.setlNode(getExp(lExp, localNode));
                            localNode.setrNode(getExp(rExp, localNode));

                        } catch (Exception e) {
                            log.error(e);
                        }
                        break;
                    }

                    case LPAR: {
                        // обрабатываем скобку
                        log.debug("Обрабатываем скобку (");
                        int p = getParEndPost(exp);
                        if (p < len) {
                            // Если выражение типа (exp) <oper> exp
                            log.debug("Обрабатываем выражение типа (exp) <oper> exp");
                            lExp = exp.subList(1, p);
                            rExp = exp.subList(p + 2, len + 1);
                            if (parrentNode != null) {
                                localNode.setLevel(parrentNode.getLevel() + 1);
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
                            localNode = (lexNode) getExp(expTemp, parrentNode);
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
                                if (parrentNode != null) {
                                    nLevel = parrentNode.getLevel() + 1;
                                }
                                // Устанавливаем уровень
                                localNode.setLevel(nLevel);
                                // Устанавливаем значение узла
                                localNode.setnToken(exp.get(i));
                                // Устанавливаем тип узла на OPER

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

    private void getNextNode(lexNode parentNode, FileWriter out, String label) throws IOException {
        log.debug("Добавляем метку : " + label);
        getNextNode(parentNode, out);
        // Добавляем метку конца блока IF        
        out.write("jmp " + label + ";\n");
        out.write(label + ":\n");
    }

    private void getNextNodeWhile(lexNode parentNode, FileWriter out, String label) throws IOException {
        log.debug("Добавляем метку : " + label);
        getNextNode(parentNode, out);
        // Добавляем метку конца блока IF        
        out.write("jmp " + label + ";\n");
        //out.write(label + ":\n");
    }

    private void getNextNode(lexNode parentNode, FileWriter out) throws IOException {
        // Если операци то выполнем генерим код асм
        log.debug("getNextNode: Если операци то выполнем генерим код асм");
        log.debug("ParentNode = " + parentNode);
        if (parentNode.getnToken().getSym() == lexerConst.tokenEnum.PLUS) {
            log.debug("Обрабатываем +");
            getNextNode((lexNode) parentNode.getlNode(), out);
            getNextNode((lexNode) parentNode.getrNode(), out);
            log.debug("add;");
            out.write("add;\n");
        }

        if (parentNode.getnToken().getSym() == lexerConst.tokenEnum.MINUS) {
            log.debug("Обрабатываем -");
            getNextNode((lexNode) parentNode.getlNode(), out);
            getNextNode((lexNode) parentNode.getrNode(), out);
            log.debug("sub;");
            out.write("sub;\n");
        }

        if (parentNode.getnToken().getSym() == lexerConst.tokenEnum.MUL) {
            // обрабатываем "-"
            log.debug("Обрабатываем *");
            getNextNode((lexNode) parentNode.getlNode(), out);
            getNextNode((lexNode) parentNode.getrNode(), out);
            log.debug("mul;");
            out.write("mul;\n");
        }

        if (parentNode.getnToken().getSym() == lexerConst.tokenEnum.EQUAL) {
            // обрабатываем "="
            log.debug("брабатываем =");
            getNextNode((lexNode) parentNode.getrNode(), out);
            log.debug("store " + ((lexNode)parentNode.getlNode()).getnToken().getVal() + ";");
            out.write("store " + ((lexNode)parentNode.getlNode()).getnToken().getVal() + ";\n");
        }

        if (parentNode.getnToken().getSym() == lexerConst.tokenEnum.WHILE) {
            // обрабатываем WHILE
            log.debug("Обрабатываем WHILE");
            // дбавлем метку дл циклла
            //this.labelCount ++;
            out.write("loop_" + this.labelCount + ":\n");
            // брабатываем услвия
            getNextNode((lexNode) parentNode.getlNode(), out);
            // обрабатываем блок перандов
            getNextNodeWhile((lexNode) parentNode.getrNode(), out, "loop_" + (this.labelCount - 1));
            out.write("label_no" + this.labelCount + ":\n");
        }

        if (parentNode.getnToken().getSym() == lexerConst.tokenEnum.FOR) {
            // обрабатываем "FOR"
            log.debug("Обрабатываем FOR");
            // Обрабатываем условие
            log.debug("Обрабатываем условие");
        }

        if (parentNode.getnToken().getSym() == lexerConst.tokenEnum.IF) {
            // обрабатываем "IF"
            log.debug("Обрабатываем IF");
            // Обрабатываем условие
            log.debug("Обрабатываем условие");
            getNextNode((lexNode) parentNode.getlNode(), out);
            // Обрабатываем ветку TRUE
            log.debug("Обрабатываем ветку TRUE");
            getNextNode((lexNode) parentNode.getrNode(), out, "label_no" + this.labelCount);
            // Обрабатываем ветку FALSE
            log.debug("Обрабатываем ветку FALSE");
            getNextNode((lexNode) parentNode.getdNode(), out, "label_no" + this.labelCount + "_end");
            //log.debug("lt label1;");
            //out.write("lt label1;\n");
        }

        if (parentNode.getnToken().getSym() == lexerConst.tokenEnum.LESS) {
            // обрабатываем "IF"
            log.debug("Обрабатываем <");
            getNextNode((lexNode) parentNode.getlNode(), out);
            getNextNode((lexNode) parentNode.getrNode(), out);
            log.debug("lt;");
            out.write("lt;\n");
            this.labelCount++;
            out.write("jz label_no" + this.labelCount + ";\n");
        }

        if (parentNode.getnToken().getSym() == lexerConst.tokenEnum.ID) {
            // обрабатываем ID     
            log.debug("обрабатываем ID");
            log.debug("frtch " + parentNode.getnToken().getVal() + ";");
            out.write("fetch " + parentNode.getnToken().getVal() + ";\n");
        }

        if (parentNode.getnToken().getSym() == lexerConst.tokenEnum.NUM) {
            // обрабатываем ID     
            log.debug("обрабатываем ID");
            out.write("push " + parentNode.getnToken().getVal() + ";\n");
        }

        if (parentNode.getnToken().getSym() == lexerConst.tokenEnum.RBRA) {
            log.debug("retr;");
            out.write("retr;\n");
        }

        if (parentNode.getnToken().getSym() == lexerConst.tokenEnum.LBRA) {
            // обрабатываем "{"
            log.debug("Обрабатываем {");

            if (parentNode.getlNode() != null) {
                getNextNode((lexNode) parentNode.getlNode(), out);
            }

            if (parentNode.getrNode() != null) {
                getNextNode((lexNode) parentNode.getrNode(), out);
                log.debug("add;");
            }
        }
    }

    public void genBlokASMText(programBlock block, FileWriter out) throws IOException {
        try {
            log.debug("Обработка блока " + block.getBlockName());
            // Получаем список операций в блоке 
            for (pNodeInterface item : block.getOperList()) {
                if (item.getType() == pNodeInterface.pNodeEnum.BLOCK) {
                    programBlock pb = (programBlock) item;
                    genBlokASMText(pb, out);
                } else {
                    getNextNode(((lexTree) item).getRootNode(), out);
                }

            }
        } catch (Exception e) {
            log.error(e);
        }
    }

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
