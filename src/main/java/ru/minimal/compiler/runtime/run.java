/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.runtime;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vasl
 */
public class run {

    private BufferedReader fr;
    private Stack<Integer> stack = new Stack<>();
    private Hashtable<String, Integer> varTable = new Hashtable();
    Pattern addPattern;
    Pattern popPattern;
    Pattern pushPattern;
    Pattern storePattern;
    Pattern fetchPattern;

    public run(String fileName) {
        try {
            initPattern();
            this.fr = new BufferedReader(new FileReader(fileName));
            String temp = "";
            while ((temp = fr.readLine()) != null) {
                System.out.println("temp = " + temp);

                if (isPush(temp)) {
                    System.out.println("push");
                    String[] operand = temp.split(" ");
                    try {
                        stack.push(Integer.parseInt(operand[1].substring(0, operand[1].length() - 1)));
                    } catch (Exception e) {
                        // Поучаем значение переменной
                        System.out.println("Переменная");
                        // Проверяем есть ли такая переменна в таблице
                        String temp1 = operand[1].substring(0, operand[1].length()-1);
                        Integer a = 0;
                        if (varTable.containsKey(temp1))
                        {
                            a = varTable.get(temp1);
                        }
                        else
                        {
                            a = 0;
                            varTable.put(temp1, a);
                        }
                        
                        stack.push(a);
                    }

                }

                if (isAdd(temp)) {
                    int op1 = stack.pop();
                    int op2 = stack.pop();
                    int res = op1 + op2;
                    stack.push(res);
                    System.out.println("add");
                }

                if (isStore(temp)) {
                    System.out.println("push");
                    String[] operand = temp.split(" ");
                    try {
                        varTable.put(operand[1].substring(0, operand[1].length() - 1), new Integer(stack.pop()));
                    } catch (Exception e) {
                        varTable.put(operand[1].substring(0, operand[1].length() - 1), new Integer(0));
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void initPattern() {
        addPattern = Pattern.compile("^add;$");
        popPattern = Pattern.compile("^pop [a-zA-Z]+;$");
        pushPattern = Pattern.compile("^push [0-9a-zA-Z]+;$");
        storePattern = Pattern.compile("^store [0-9a-zA-Z]+;$");
        fetchPattern = Pattern.compile("^fetch [0-9a-zA-Z]+;$");
    }

    private boolean isAdd(String command) {
        boolean res = false;
        Matcher m = this.addPattern.matcher(command);
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

    public Hashtable<String, Integer> getVarTable() {
        return varTable;
    }
}
