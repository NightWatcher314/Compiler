package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.ir.IRImmediate;
import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.ir.Instruction;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.parser.table.Symbol;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// TODO: 实验三: 实现 IR 生成

/**
 *
 */
public class IRGenerator implements ActionObserver {

    private SymbolTable symbolTable;
    private Stack<Symbol> symbolStack = new Stack<>();
    private List<Instruction> instructionList = new ArrayList<>();

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        symbolStack.push(new Symbol(currentToken));
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        switch (production.index()) {
            case 6 -> {
                IRValue E = symbolStack.pop().getIrValue();
                symbolStack.pop();

                IRVariable id = IRVariable.named(symbolStack.pop().getToken().getText());
                symbolStack.push(new Symbol());
                Instruction instruction = Instruction.createMov(id, E);
                instructionList.add(instruction);
            }
            case 7 -> {
                IRValue E = symbolStack.pop().getIrValue();
                symbolStack.pop();

                symbolStack.push(new Symbol());
                Instruction instruction = Instruction.createRet(E);
                instructionList.add(instruction);
            }
            case 8 -> {
                IRVariable E = IRVariable.temp();
                IRValue A = symbolStack.pop().getIrValue();
                symbolStack.pop();

                IRValue E1 = symbolStack.pop().getIrValue();
                symbolStack.push(new Symbol(E));
                Instruction instruction = Instruction.createAdd(E, E1, A);
                instructionList.add(instruction);
            }
            case 9 -> {
                IRVariable E = IRVariable.temp();
                IRValue A = symbolStack.pop().getIrValue();
                symbolStack.pop();

                IRValue E1 = symbolStack.pop().getIrValue();
                symbolStack.push(new Symbol(E));
                Instruction instruction = Instruction.createSub(E, E1, A);
                instructionList.add(instruction);
            }
            case 10 -> {

            }
            case 11 -> {
                IRVariable A = IRVariable.temp();
                IRValue B = symbolStack.pop().getIrValue();
                symbolStack.pop();

                IRValue A1 = symbolStack.pop().getIrValue();
                symbolStack.push(new Symbol(A));
                Instruction instruction = Instruction.createMul(A, A1, B);
                instructionList.add(instruction);
            }
            case 12 -> {

            }
            case 13 -> {
                symbolStack.pop();
                Symbol symbol = symbolStack.pop();
                symbolStack.pop();
                symbolStack.push(symbol);
            }
            case 14 -> {
                IRVariable irVariable = IRVariable.named(symbolStack.pop().getToken().getText());
                symbolStack.push(new Symbol(irVariable));
            }
            case 15 -> {
                IRImmediate irImmediate = IRImmediate.of(Integer.valueOf(symbolStack.pop().getToken().getText()));
                symbolStack.push(new Symbol(irImmediate));
            }
            default -> {
                symbolStack.removeAllElements();
                symbolStack.push(new Symbol());
            }
        }
    }


    @Override
    public void whenAccept(Status currentStatus) {
        // TODO
//        throw new NotImplementedException();
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        this.symbolTable = table;
    }

    public List<Instruction> getIR() {
        // TODO
        return instructionList;
    }

    public void dumpIR(String path) {
        FileUtils.writeLines(path, getIR().stream().map(Instruction::toString).toList());
    }
}

