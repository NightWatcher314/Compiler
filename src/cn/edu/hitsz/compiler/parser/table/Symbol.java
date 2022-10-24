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

    private Symbol(Token token, NonTerminal nonTerminal){
        this.token = token;
        this.nonTerminal = nonTerminal;
    }

    public Symbol(Token token){
        this(token, null);
    }

    public Symbol(NonTerminal nonTerminal){
        this(null, nonTerminal);
    }


    public boolean isToken(){
        return this.token != null;
    }

    public Token getToken() {
        if(token == null){
            throw new RuntimeException();
        }
        return token;
    }
}
