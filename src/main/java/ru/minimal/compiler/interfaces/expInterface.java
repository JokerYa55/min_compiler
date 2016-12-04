/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.interfaces;

import ru.minimal.compiler.lexer.lexerConst.expTypeEnum;

/**
 *
 * @author vasl
 */
public interface expInterface {

    /**
     *
     * @return 
     */
    public expTypeEnum getType();
    public void setType(expTypeEnum expType);
}
