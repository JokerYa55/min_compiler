/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.comiler;

import ru.minimal.compiler.lexer.lexerConst;

/**
 *
 * @author vasl
 */
public class programStract {

    private programBlock pblock;

    public programStract() {
        this.pblock = new programBlock(null);
        this.pblock.setBlockName("main");
    }

    public programBlock getPblock() {
        return pblock;
    }

    public programBlock newPrograBlock(programBlock parrentBlock, String name)
    {
        programBlock res;
        res = new programBlock(parrentBlock);
        res.setType(lexerConst.expTypeEnum.BLOCK);
        res.setBlockName(name);
        parrentBlock.setpBlock(res);
        parrentBlock.addOper(res);
        return res;
    }
    
    @Override
    public String toString() {
        return pblock.toString();
    }


}
