package cn.edu.hitsz.compiler.parser.table;

import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;

/**
 * @author night
 */
public class Symbol {
    private final Token token;
    private final NonTerminal nonTerminal;
    private final SourceCodeType sourceCodeType;
    private final IRValue irValue;

    private Symbol(Token token, NonTerminal nonTerminal, SourceCodeType sourceCodeType, IRValue irValue) {
        this.token = token;
        this.nonTerminal = nonTerminal;
        this.sourceCodeType = sourceCodeType;
        this.irValue = irValue;
    }

    public Symbol(Token token) {
        this(token, null, null, null);
    }

    public Symbol(NonTerminal nonTerminal) {
        this(null, nonTerminal, null, null);
    }

    public Symbol(SourceCodeType sourceCodeType) {
        this(null, null, sourceCodeType, null);
    }

    public Symbol(IRValue irValue) {
        this(null, null, null, irValue);
    }

    public Symbol() {
        this(null, null, null, null);
    }

    public boolean isToken() {
        return this.token != null;
    }

    public Token getToken() {
        if (token == null) {
            throw new RuntimeException();
        }
        return token;
    }

    public SourceCodeType getSourceCodeType() {
        if (sourceCodeType == null) {
            throw new RuntimeException();
        }
        return sourceCodeType;
    }

    public IRValue getIrValue() {
        if (irValue == null) {
            throw new RuntimeException();
        }
        return irValue;
    }
}
