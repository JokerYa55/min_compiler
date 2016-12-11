/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.runtime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
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
    private Stack<Long> stack = new Stack<>();
    private HashMap<String, Long> varTable = new HashMap();
    private Pattern addPattern;
    private Pattern subPattern;
    private Pattern popPattern;
    private Pattern pushPattern;
    private Pattern storePattern;
    private Pattern fetchPattern;
    private Pattern mulPattern;
    private final String fileName;        // имя исполняемого файла
    // Служебные регистры
    private int CS = 0; //Сегмент кода указывает на текущую команду
    // Программа в памяти
    List<String> commandList = new ArrayList();

    private void loadCommand() {
        try {
            // Открываем файл с ASM кодом на исполнение
            this.fr = new BufferedReader(new FileReader(this.fileName));

            String temp = "";
            int i = 0;
            // Читаем команды и загружаем в commandList

            while ((temp = fr.readLine()) != null) {
                i++;
                log.debug(i + ": temp = " + temp);
                commandList.add(temp);
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(run.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public run(String fileName) {
        this.fileName = fileName;
        runProc();
    }

    @Override
    public void runProc() {
        try {
            // Инициализируем шаблоны команд для парсинга команд;
            initPattern();
            loadCommand();
            String temp = "";
            while (this.CS < commandList.size()) {
                temp = commandList.get(CS);
                if (isPush(temp)) {
                    log.debug("push");
                    String[] operand = temp.split(" ");
                    try {
                        stack.push(Long.parseLong(operand[1].substring(0, operand[1].length() - 1)));
                        this.CS++;
                    } catch (Exception e) {
                        // Поучаем значение переменной
                        log.debug("Переменная");
                        // Проверяем есть ли такая переменна в таблице
                        String temp1 = operand[1].substring(0, operand[1].length() - 1);
                        long a = 0;
                        if (varTable.containsKey(temp1)) {
                            a = varTable.get(temp1);
                        } else {
                            a = 0;
                            varTable.put(temp1, a);
                        }
                        stack.push(a);
                        this.CS++;
                    }
                }

                if (isAdd(temp)) {
                    Long op1 = stack.pop();
                    Long op2 = stack.pop();
                    log.debug("add : " + op1 + "+" + op2);
                    Long res = op1 + op2;
                    log.debug("res = " + res);
                    stack.push(res);
                    this.CS++;
                }

                if (isSub(temp)) {
                    Long op1 = stack.pop();
                    Long op2 = stack.pop();
                    log.debug("sub : " + op2 + "-" + op1);
                    Long res = op2 - op1;
                    log.debug("res = " + res);
                    stack.push(res);
                    this.CS++;
                }

                if (isMul(temp)) {
                    Long op1 = stack.pop();
                    Long op2 = stack.pop();
                    log.debug("mul : " + op2 + "*" + op1);
                    Long res = op2 * op1;
                    log.debug("res = " + res);
                    stack.push(res);
                    this.CS++;
                }

                if (isStore(temp)) {
                    log.debug("store = " + temp);
                    String[] operand = temp.split(" ");
                    try {
                        varTable.put(operand[1].substring(0, operand[1].length() - 1), new Long(stack.pop()));
                        this.CS++;
                    } catch (Exception e) {
                        varTable.put(operand[1].substring(0, operand[1].length() - 1), new Long(0));
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
        mulPattern = Pattern.compile("^mul;$");
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

    private boolean isMul(String command) {
        boolean res = false;
        Matcher m = this.mulPattern.matcher(command);
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
        for (Long itemKey : stack) {
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

    public HashMap<String, Long> getVarTable() {
        return varTable;
    }
}
