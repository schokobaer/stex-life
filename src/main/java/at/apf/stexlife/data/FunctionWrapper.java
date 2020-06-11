package at.apf.stexlife.data;

import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;

import java.util.List;

public class FunctionWrapper {

    private StexLifeGrammarParser.FunctionContext named;
    private StexLifeGrammarParser.AnonymousFunctionContext anonymous;

    public FunctionWrapper(StexLifeGrammarParser.FunctionContext ctx) {
        named = ctx;
    }

    public FunctionWrapper(StexLifeGrammarParser.AnonymousFunctionContext ctx) {
        anonymous = ctx;
    }

    public String getName() {
        return named != null ? named.ID().getText() : "anonymous";
    }

    public StexLifeGrammarParser.ParamListContext getParamList() {
        return named != null ? named.paramList() : anonymous.paramList();
    }

    public List<StexLifeGrammarParser.StmtContext> getStmt() {
        return named != null ? named.stmt() : anonymous.stmt();
    }
}
