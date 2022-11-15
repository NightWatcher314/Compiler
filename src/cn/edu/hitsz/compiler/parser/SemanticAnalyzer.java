package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.parser.table.Symbol;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;
import cn.edu.hitsz.compiler.symtab.SymbolTable;

import java.util.Stack;

public class SemanticAnalyzer implements ActionObserver {

    private SymbolTable symbolTable;
    private Stack<Symbol> symbolStack=new Stack<>();


    @Override
    public void whenAccept(Status currentStatus) {
        // TODO: 该过程在遇到 Accept 时要采取的代码动作
//        throw new NotImplementedException();
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        switch (production.index()){
            case 4 -> {
                Token token = symbolStack.pop().getToken();
                SourceCodeType sourceCodeType = symbolStack.pop().getSourceCodeType();
                symbolTable.get(token.getText()).setType(sourceCodeType);
                symbolStack.push(new Symbol());
            }
            case 5 -> {
                symbolStack.pop();
                symbolStack.push(new Symbol(SourceCodeType.Int));
            }
            default -> {
                symbolStack.removeAllElements();
                symbolStack.push(new Symbol());
            }
        }
    }

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        symbolStack.push(new Symbol(currentToken));
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        this.symbolTable=table;
    }
}

