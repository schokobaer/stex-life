package at.apf.stexlife.data;

import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;

import java.util.List;

public class FunctionWrapper {

    private StexLifeGrammarParser.FunctionContext function;
    private StexLifeGrammarParser.AnonymousFunctionContext anonymous;
    private String named;

    public FunctionWrapper(StexLifeGrammarParser.FunctionContext ctx) {
        function = ctx;
    }

    public FunctionWrapper(StexLifeGrammarParser.AnonymousFunctionContext ctx) {
        anonymous = ctx;
    }

    public FunctionWrapper(String named) {
        this.named = named;
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

    public List<StexLifeGrammarParser.StmtContext> getStmt() {
        return function != null ? function.stmt() : anonymous.stmt();
    }
}
