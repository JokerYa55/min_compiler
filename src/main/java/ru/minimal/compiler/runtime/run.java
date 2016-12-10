/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.runtime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import ru.minimal.compiler.interfaces.processorInterfaces;

/**
 *
 * @author vasl
 */
public class run implements processorInterfaces {

    static Logger log = Logger.getLogger(run.class);
    private BufferedReader fr;
    private Stack<Integer> stack = new Stack<>();
    private HashMap<String, Integer> varTable = new HashMap();
    private Pattern addPattern;
    private Pattern subPattern;
    private Pattern popPattern;
    private Pattern pushPattern;
    private Pattern storePattern;
    private Pattern fetchPattern;
    private final String fileName;        // имя исполняемого файла

    public run(String fileName) {
        this.fileName = fileName;
        runProc();
    }

    @Override
    public void runProc() {
        try {
            // Инициализируем шаблоны команд для парсинга команд;
            initPattern();
            // Открываем файл с ASM кодом на исполнение
            this.fr = new BufferedReader(new FileReader(this.fileName));
            String temp = "";
            int i = 0;
            // Читаем команды и исполняем их
            while ((temp = fr.readLine()) != null) {
                i++;
                log.debug(i + ": temp = " + temp);

                if (isPush(temp)) {
                    log.debug("push");
                    String[] operand = temp.split(" ");
                    try {
                        stack.push(Integer.parseInt(operand[1].substring(0, operand[1].length() - 1)));
                    } catch (Exception e) {
                        // Поучаем значение переменной
                        log.debug("Переменная");
                        // Проверяем есть ли такая переменна в таблице
                        String temp1 = operand[1].substring(0, operand[1].length() - 1);
                        Integer a = 0;
                        if (varTable.containsKey(temp1)) {
                            a = varTable.get(temp1);
                        } else {
                            a = 0;
                            varTable.put(temp1, a);
                        }
                        stack.push(a);
                    }
                }

                if (isAdd(temp)) {
                    int op1 = stack.pop();
                    int op2 = stack.pop();
                    log.debug("add : " + op1 + "+" + op2);
                    int res = op1 + op2;
                    log.debug("res = " + res);
                    stack.push(res);

                }

                if (isSub(temp)) {
                    int op1 = stack.pop();
                    int op2 = stack.pop();
                    log.debug("sub : " + op2 + "-" + op1);
                    int res = op2 - op1;
                    log.debug("res = " + res);
                    stack.push(res);

                }

                if (isStore(temp)) {
                    log.debug("store = " + temp);
                    String[] operand = temp.split(" ");
                    try {
                        varTable.put(operand[1].substring(0, operand[1].length() - 1), new Integer(stack.pop()));
                    } catch (Exception e) {
                        varTable.put(operand[1].substring(0, operand[1].length() - 1), new Integer(0));
                    }
                }
                log.info(getStack());
            }
        } catch (Exception e) {
            log.error("Ошибка: " + e.getMessage());
        }
    }

    // Инициализация шаблонов команд
    private void initPattern() {
        log.debug("initPattern");
        addPattern = Pattern.compile("^add;$");
        subPattern = Pattern.compile("^sub;$");
        popPattern = Pattern.compile("^pop [a-zA-Z]+;$");
        pushPattern = Pattern.compile("^push [0-9a-zA-Z\\.]+;$");
        storePattern = Pattern.compile("^store [0-9a-zA-Z\\._]+;$");
        fetchPattern = Pattern.compile("^fetch [0-9a-zA-Z]+;$");
    }

    private boolean isAdd(String command) {
        boolean res = false;
        Matcher m = this.addPattern.matcher(command);
        res = m.matches();
        return res;
    }

    private boolean isSub(String command) {
        boolean res = false;
        Matcher m = this.subPattern.matcher(command);
        res = m.matches();
        return res;
    }

    private boolean isPop(String command) {
        boolean res = false;
        Matcher m = this.popPattern.matcher(command);
        res = m.matches();
        return res;
    }

    private boolean isPush(String command) {
        boolean res = false;
        Matcher m = this.pushPattern.matcher(command);
        res = m.matches();
        return res;
    }

    private boolean isFetch(String command) {
        boolean res = false;
        Matcher m = this.fetchPattern.matcher(command);
        res = m.matches();
        return res;
    }

    private boolean isStore(String command) {
        boolean res = false;
        Matcher m = this.storePattern.matcher(command);
        res = m.matches();
        return res;
    }

    // Получение текста программы
    @Override
    public String getProgramText() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // Получение текущего стека программы
    @Override
    public String getStack() {
        log.debug("getStack()");
        String res = "Состояние стека :\n";
        int i = 0;
        for (Integer itemKey : stack) {
            i++;
            res = res + i + " : " + itemKey + "\n";
        }
        return res;
    }

    // Получение таблицы переменных программы
    @Override
    public String getVariable() {
        log.debug("getVariable()");
        String res = "Переменные : \n";
        try {
            for (String itemKey : this.getVarTable().keySet()) {
                res = res + itemKey + "\t= " + this.getVarTable().get(itemKey) + "\n";
            }
            //log.info(res);
        } catch (Exception e) {
            log.error("Ошибка : " + e.getMessage());
        }
        return res;
    }

    public HashMap<String, Integer> getVarTable() {
        return varTable;
    }
}
