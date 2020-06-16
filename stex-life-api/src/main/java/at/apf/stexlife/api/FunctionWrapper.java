package at.apf.stexlife.api;

import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;
import org.apache.commons.lang3.tuple.Pair;

public class FunctionWrapper {

    private StexLifeGrammarParser.FunctionContext function;
    private StexLifeGrammarParser.AnonymousFunctionContext anonymous;
    private Pair<String, String> plugin; // <module, function>
    private DataUnit ctx;
    private ModuleWrapper module;

    public FunctionWrapper(StexLifeGrammarParser.FunctionContext ctx, ModuleWrapper module) {
        function = ctx;
        this.module = module;
    }

    public FunctionWrapper(StexLifeGrammarParser.AnonymousFunctionContext ctx, ModuleWrapper module) {
        anonymous = ctx;
        this.module = module;
    }

    public FunctionWrapper(Pair<String, String> plugin) {
        this.plugin = plugin;
    }

    public DataUnit getCtx() {
        return ctx;
    }

    public void setCtx(DataUnit ctx) {
        this.ctx = ctx;
    }

    public boolean isAnonymous() {
        return anonymous != null;
    }

    public boolean isPluginFunction() {
        return plugin != null;
    }

    public String getName() {
        return function != null ? function.ID().getText() :
                anonymous != null ? "anonymous" : plugin.getValue();
    }

    public StexLifeGrammarParser.ParamListContext getParamList() {
        return function != null ? function.paramList() :
                anonymous != null ? anonymous.paramList() : null;
    }

    public StexLifeGrammarParser.BlockContext getBlock() {
        return function != null ? function.block() : anonymous.block();
    }

    public Pair<String, String> getPlugin() {
        return plugin;
    }

    public ModuleWrapper getModule() {
        return module;
    }
}
