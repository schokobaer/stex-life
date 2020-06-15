package at.apf.stexlife.api;

import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;

import java.util.List;

public class FunctionWrapper {

    private StexLifeGrammarParser.FunctionContext function;
    private StexLifeGrammarParser.AnonymousFunctionContext anonymous;
    private String named;
    private DataUnit ctx;

    public FunctionWrapper(StexLifeGrammarParser.FunctionContext ctx) {
        function = ctx;
    }

    public FunctionWrapper(StexLifeGrammarParser.AnonymousFunctionContext ctx) {
        anonymous = ctx;
    }

    public FunctionWrapper(String named) {
        this.named = named;
    }

    public DataUnit getCtx() {
        return ctx;
    }

    public void setCtx(DataUnit ctx) {
        this.ctx = ctx;
    }

    public boolean isNamed() {
        return named != null;
    }

    public String getName() {
        return function != null ? function.ID().getText() :
                anonymous != null ? "anonymous" : named;
    }

    public StexLifeGrammarParser.ParamListContext getParamList() {
        return function != null ? function.paramList() :
                anonymous != null ? anonymous.paramList() : null;
    }

    public StexLifeGrammarParser.BlockContext getBlock() {
        return function != null ? function.block() : anonymous.block();
    }
}
