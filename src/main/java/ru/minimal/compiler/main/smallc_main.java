/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.main;

import java.io.FileReader;
import org.apache.log4j.Logger;
import ru.minimal.compiler.comiler.compiller_c;
import ru.minimal.compiler.comiler.compiller_exp;
import ru.minimal.compiler.comiler.lexTree;
import ru.minimal.compiler.comiler.programStract;
import ru.minimal.compiler.interfaces.compilerInterface;
import ru.minimal.compiler.lexer.lexer;
import ru.minimal.compiler.runtime.run;

/**
 *
 * @author vasl
 */
public class smallc_main {

    /**
     * @param args the command line arguments
     */
    static Logger log = Logger.getLogger(smallc_main.class);

    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("");
        // TODO code application logic here
        log.debug("Запуск компиятора");

        try (FileReader fin = new FileReader(args[0])) {
            int c;
            lexer lex = new lexer(fin);
            compilerInterface comp = new compiller_exp();
            
            //compiller_c comp = new compiller_c();
            programStract tree = comp.genlexTree(lex.getTokenList());
            System.out.println(tree.toString());
            /*comp.genASText(args[1]);

            // Запускаем программу 
            run runnable = new run(args[1]);
            
            System.out.println(runnable.getVariable());*/

            /*while ((c = fin.read())!=-1)
            {
                System.out.println("tep = " + (char) c);
            }*/
        } catch (Exception e) {
            log.error(e);
        }
    }

}
