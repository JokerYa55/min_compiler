/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.minimal.compiler.comiler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import ru.minimal.compiler.interfaces.expInterface;
import ru.minimal.compiler.lexer.lexerConst;

/**
 *
 * @author vasl programBlock - блок операторов объедененных логически. Всегда
 * есть осноовной блок содержащий все операторы состоит из списка операторов
 * (синтаксических деревьев разбора оператора)
 */
public class programBlock implements Iterable<lexTree>, expInterface {

    private List<expInterface> operList;
    //Ссылка на родительский блок
    private programBlock pBlock;
    private String blockName;
    private lexerConst.expTypeEnum expType;
    
    public programBlock(programBlock parentBlock) {
        this.pBlock = parentBlock;
        this.operList = new ArrayList<>();
    }

    public boolean addOper(expInterface oper) {
        boolean res = false;        
        res = this.operList.add(oper);
        return res;
    }

    public List<expInterface> getOperList() {
        return operList;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    @Override
    public String toString() {
        String res = null;
        res = res + "Блок = " + blockName + "{\n";
        for (expInterface item : this.operList) {
            res = res + item.toString();
        }
        return res + "}\n";
    }

    
    
    @Override
    public Iterator<lexTree> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void forEach(Consumer<? super lexTree> action) {
        Iterable.super.forEach(action); //To change body of generated methods, choose Tools | Templates.
    }

    public programBlock getpBlock() {
        return pBlock;
    }

    public void setpBlock(programBlock pBlock) {
        this.pBlock = pBlock;
    }

    @Override
    public lexerConst.expTypeEnum getType() {
        return this.expType;
    }

    @Override
    public void setType(lexerConst.expTypeEnum expType) {
        this.expType = expType;
    }
}
