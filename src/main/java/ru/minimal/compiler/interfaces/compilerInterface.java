/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.interfaces;

import java.util.List;
import ru.minimal.compiler.comiler.programStract;
import ru.minimal.compiler.lexer.token;

/**
 *
 * @author vasl
 */
public interface compilerInterface {

    programStract genlexTree(List<token> listToken);

}
