package at.apf.stexlife;

import at.apf.stexlife.api.Converter;
import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.FunctionWrapper;
import at.apf.stexlife.api.StexLifeVM;
import at.apf.stexlife.api.exception.FunctionNotFoundException;
import at.apf.stexlife.api.exception.InvalidTypeException;
import at.apf.stexlife.api.exception.StexLifeException;
import at.apf.stexlife.exception.UncaughtExceptionException;
import at.apf.stexlife.parser.StexLifeCodeParser;
import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;
import at.apf.stexlife.runtime.Arithmetics;
import at.apf.stexlife.runtime.DataFrame;
import at.apf.stexlife.runtime.ExpressionWrapper;
import at.apf.stexlife.api.ModuleWrapper;
import at.apf.stexlife.runtime.StexFrame;
import at.apf.stexlife.runtime.StmtResult;
import at.apf.stexlife.runtime.exception.NameAlreadyDeclaredException;
import at.apf.stexlife.runtime.exception.NameNotFoundException;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VMImpl implements StexLifeVM {

    private StexFrame stexFrame;
    private PluginRegistry pluginRegistry;
    private Map<String, ModuleWrapper> modules = new HashMap<>(); // <moduleName, Module>
    private ModuleWrapper mainModule;
    private static StexLifeCodeParser parser = new StexLifeCodeParser();

    public VMImpl(StexLifeGrammarParser.ProgramContext program) {
        this(program, new PluginRegistryImpl());
    }

    public VMImpl(StexLifeGrammarParser.ProgramContext program, PluginRegistry pluginRegistry) {
        mainModule = new ModuleWrapper(program);
        this.pluginRegistry = pluginRegistry;
    }

    @Override
    public DataUnit run(String function) throws UncaughtExceptionException {
        return this.run(function, new DataUnit[0]);
    }

    @Override
    public DataUnit run(String function, DataUnit[] data) throws UncaughtExceptionException {
        Optional<StexLifeGrammarParser.FunctionContext> fun = mainModule.getProgram().function().stream()
                .filter(f -> f.ID().getText().equals(function))
                .findFirst();
        if (fun.isPresent()) {
            try {
                return run(new FunctionWrapper(fun.get(), mainModule), data);
            } catch (StexLifeException e) {
                throw new UncaughtExceptionException(e);
            }
        } else {
            throw new UncaughtExceptionException(new FunctionNotFoundException(function));
        }
    }

    @Override
    public DataUnit run(FunctionWrapper function, DataUnit[] args) {
        if (function.isPluginFunction()) {
            Pair<String, String> plugin = function.getPlugin();
            if (pluginRegistry.isRegistered(plugin.getLeft(), plugin.getRight())) {
                return pluginRegistry.call(this, plugin.getLeft(), plugin.getRight(), args);
            }
            throw new FunctionNotFoundException(function.getName());
        }

        DataFrame df = new DataFrame(null);
        for (int i = 0; i < function.getParamList().ID().size(); i++) {
            df.set(function.getParamList().ID(i).getText(),
                    i < args.length ? args[i] : DataUnit.UNDEFINED);
        }

        stexFrame = new StexFrame(stexFrame, df);
        stexFrame.setModule(function.getModule());
        stexFrame.setSelf(function.getCtx());
        DataUnit result = runFunction(function);
        stexFrame = stexFrame.getParent();

        return result;
    }

    @Override
    public void loadIncludes() throws IOException {
        /*mainModule.getProgram().include().forEach(i -> {
            if (i.FROM() != null) {
                i.includeDeclaration().forEach(dec -> {
                    String usedName = dec.alias() != null ? dec.alias().ID().getText() : dec.ID().getText();
                    if (mainModule.getIncludes().containsKey(usedName)) {
                        throw new NameAlreadyDeclaredException(usedName);
                    }
                    mainModule.getIncludes().put(usedName, new ImmutablePair<>(i.includeSource().ID().getText(), dec.ID().getText()));
                });
            } else {
                // register all functions from module
                String module = i.includeSource().ID().getText();
                String alias = i.alias() != null ? i.alias().ID().getText() : module;
                pluginRegistry.getRegistrations(module).stream().forEach(fun -> {
                    mainModule.getIncludes().put(alias + "." + fun, new ImmutablePair<>(module, fun));
                });
            }
        });*/
        loadModuleIncludes(mainModule);
    }

    /**
     * Loads recursive the included stex modules.
     * @param file
     * @return
     * @throws IOException
     */
    private ModuleWrapper loadModule(File file) throws IOException {
        StexLifeGrammarParser.ProgramContext program = parser.parse(file);
        ModuleWrapper module = new ModuleWrapper(program);
        if (modules.containsKey(module.getName())) {
            return modules.get(module.getName());
        }
        modules.put(module.getName(), module);

        // load includes
        loadModuleIncludes(module);

        return module;
    }

    /**
     * Adds all functions to the includes and loads recursive included modules.
     * @param module Module to handle the includes.
     * @throws IOException When a subModule which has to be loaded throws an IOException.
     */
    private void loadModuleIncludes(ModuleWrapper module) throws IOException {
        for (StexLifeGrammarParser.IncludeContext i: module.getProgram().include()) {
            if (i.FROM() != null) {
                // only register selected ones
                String moduleName;
                if (i.includeSource().ID() != null) {
                    // plugin functions
                    moduleName = i.includeSource().ID().getText();
                } else {
                    // module
                    // TODO: Handle file path correctly (relative to current file location)
                    String filePath = i.includeSource().STRING().getText();
                    filePath = filePath.substring(1, filePath.length() - 1);
                    ModuleWrapper subModule = loadModule(new File(filePath));
                    moduleName = subModule.getName();
                }
                i.includeDeclaration().forEach(dec -> {
                    String usedName = dec.alias() != null ? dec.alias().ID().getText() : dec.ID().getText();
                    if (module.getIncludes().containsKey(usedName)) {
                        throw new NameAlreadyDeclaredException(usedName);
                    }
                    module.getIncludes().put(usedName, new ImmutablePair<>(moduleName, dec.ID().getText()));
                });
            } else {
                // register all functions from module
                String moduleName;
                String alias;
                List<String> exportedFunctions;
                if (i.includeSource().ID() != null) {
                    // plugin functions
                    moduleName = i.includeSource().ID().getText();
                    exportedFunctions = pluginRegistry.getRegistrations(moduleName);
                } else {
                    // module
                    String filePath = i.includeSource().STRING().getText();
                    filePath = filePath.substring(1, filePath.length() - 1);
                    ModuleWrapper subModule = loadModule(new File(filePath));
                    moduleName = subModule.getName();
                    exportedFunctions = subModule.getExportedFunctions();
                }

                alias = i.alias() != null ? i.alias().ID().getText() : moduleName;
                exportedFunctions.stream().forEach(fun -> {
                    module.getIncludes().put(alias + "." + fun, new ImmutablePair<>(moduleName, fun));
                });
            }
        }
    }

    private DataUnit resolveIdentifier(StexLifeGrammarParser.IdentifierContext ctx) {
        // Check if theres a variable in the dataframe
        // Else check if theres a local function
        // else check if theres an import
        // return undefined

        List<TerminalNode> ids = ctx.ID();
        DataUnit dataUnit = null;
        int start = 0;
        if (ctx.SELF() != null) {
            if (stexFrame.getSelf() == null) {
                //throw new NameNotFoundException("this");
                dataUnit = DataUnit.UNDEFINED;
            }
            dataUnit = stexFrame.getSelf();
        } else {
            try {
                dataUnit = stexFrame.getDataFrame().get(ids.get(0).getText());
            } catch (NameNotFoundException e) {
                dataUnit = null;
            }
            start = 1;
        }

        if (dataUnit != null) {
            // resolve object path
            for (int i = start; i < ids.size(); i++) {
                String name = ids.get(i).getText();
                if (dataUnit.getType() != DataType.OBJECT || !dataUnit.getObject().containsKey(name)) {
                    return DataUnit.UNDEFINED;
                }
                dataUnit = dataUnit.getObject().get(name);
            }
            return dataUnit;
        }

        // check for local function
        String name = ctx.getText();
        Optional<StexLifeGrammarParser.FunctionContext> op = stexFrame.getModule().getProgram().function().stream()
                .filter(f -> f.ID().getText().equals(name)).findFirst();

        if (op.isPresent()) {
            return new DataUnit(new FunctionWrapper(op.get(), stexFrame.getModule()), DataType.FUNCTION);
        }

        // check in includes
        Map<String, Pair<String, String>> includes = stexFrame.getModule().getIncludes();
        if (includes.containsKey(name)) {
            // check in plugin registry
            if (pluginRegistry.isRegistered(includes.get(name).getLeft(), includes.get(name).getRight())) {
                return new DataUnit(new FunctionWrapper(
                        new ImmutablePair<>(includes.get(name).getLeft(), includes.get(name).getRight())), DataType.FUNCTION);
            }

            // find in modules
            if (modules.containsKey(includes.get(name).getLeft())) {
                ModuleWrapper mod = modules.get(includes.get(name).getLeft());
                op = mod.getProgram().function().stream()
                        .filter(f -> f.ID().getText().equals(includes.get(name).getRight()))
                        .findFirst();
                if (op.isPresent()) {
                    return new DataUnit(new FunctionWrapper(op.get(), mod), DataType.FUNCTION);
                }
            }
        }

        return DataUnit.UNDEFINED;
    }

    private DataUnit runFunction(FunctionWrapper f) {
        runBlock(f.getBlock());
        return stexFrame.getResult();
    }

    private StmtResult runStmt(StexLifeGrammarParser.StmtContext stmt) {
        if (stmt.declareStmt() != null) {
            return runDeclareStmt(stmt.declareStmt());
        } else if (stmt.assignStmt() != null) {
            return runAssignStmt(stmt.assignStmt());
        }  else if (stmt.block() != null) {
            return runBlock(stmt.block());
        } else if (stmt.ifStmt() != null) {
            return runIfStmt(stmt.ifStmt());
        } else if (stmt.whileStmt() != null) {
            return runWhileStmt(stmt.whileStmt());
        } else if (stmt.forStmt() != null) {
            return runForStmt(stmt.forStmt());
        } else if (stmt.foreachStm() != null) {
            return runForeachStmt(stmt.foreachStm());
        } else if (stmt.voidFunctionCall() != null) {
            evalFunctionCall(stmt.voidFunctionCall().functionCall());
            return StmtResult.NONE;
        } else if (stmt.tryStmt() != null) {
            return runTryStmt(stmt.tryStmt());
        } else if (stmt.throwStmt() != null) {
            runThrowStmt(stmt.throwStmt());
        } else if (stmt.returnStmt() != null) {
            return runReturnStmt(stmt.returnStmt());
        } else if (stmt.BREAK() != null) {
            return StmtResult.BREAK;
        } else if (stmt.CONTINUE() != null) {
            return StmtResult.CONTINUE;
        }
        return StmtResult.NONE;
    }

    private StmtResult runDeclareStmt(StexLifeGrammarParser.DeclareStmtContext ctx) {
        String name = ctx.ID().getText();
        if (stexFrame.getDataFrame().contains(name)) {
            throw new NameAlreadyDeclaredException(name);
        }
        DataUnit value = evalExpression(ctx.expression());
        stexFrame.getDataFrame().set(name, value);
        return StmtResult.NONE;
    }

    private StmtResult runAssignStmt(StexLifeGrammarParser.AssignStmtContext ctx) {
        DataUnit value = evalExpression(ctx.expression());
        if (ctx.assignee().identifier() != null) {
            // override the old value
            List<TerminalNode> ids = ctx.assignee().identifier().ID();
            if (ids.size() == 1) {
                if (ctx.shortAssignOp() != null) {
                    value = evalShortAssignOperation(ctx.shortAssignOp(),
                            stexFrame.getDataFrame().get(ids.get(0).getText()), value);
                }
                stexFrame.getDataFrame().set(ids.get(0).getText(), value);
            } else {
                DataUnit obj = stexFrame.getDataFrame().get(ids.get(0).getText());
                DataType.expecting(obj, DataType.OBJECT);
                for (int i = 1; i < ids.size() - 1; i++) {
                    obj = obj.getObject().get(ids.get(i).getText());
                    DataType.expecting(obj, DataType.OBJECT);
                }
                if (value.getType() == DataType.FUNCTION &&
                        (ctx.expression().selfFunctionRef() != null || ctx.expression().anonymousFunction() != null)) {
                    value.getFunction().setCtx(obj);
                }
                if (ctx.shortAssignOp() != null) {
                    value = evalShortAssignOperation(ctx.shortAssignOp(),
                            obj.getObject().get(ids.get(ids.size() - 1).getText()), value);
                }
                obj.getObject().put(ids.get(ids.size() - 1).getText(), value);
            }
        } else {
            // dynamic access
            DataUnit data = resolveIdentifier(ctx.assignee().dynamicAccess().identifier());
            DataUnit index = evalExpression(ctx.assignee().dynamicAccess().expression());

            if (data.getType() == DataType.ARRAY) {
                DataType.expecting(index, DataType.INT);
                if (ctx.shortAssignOp() != null) {
                    value = evalShortAssignOperation(ctx.shortAssignOp(),
                            data.getArray().get(index.getInt().intValue()), value);
                }
                data.getArray().set(index.getInt().intValue(), value);
                return StmtResult.NONE;
            }

            if (data.getType() == DataType.OBJECT) {
                DataType.expecting(index, DataType.STRING);
                if (value.getType() == DataType.FUNCTION &&
                        (ctx.expression().selfFunctionRef() != null || ctx.expression().anonymousFunction() != null)) {
                    value.getFunction().setCtx(data);
                }
                if (ctx.shortAssignOp() != null) {
                    value = evalShortAssignOperation(ctx.shortAssignOp(),
                            data.getObject().get(index.getString()), value);
                }
                data.getObject().put(index.getString(), value);
                return StmtResult.NONE;
            }

            DataType.expecting(data, DataType.ARRAY, DataType.OBJECT);
        }
        return StmtResult.NONE;
    }

    private StmtResult runBlock(StexLifeGrammarParser.BlockContext ctx) {
        stexFrame.enterDataFrame();
        for (StexLifeGrammarParser.StmtContext stmt: ctx.stmt()) {
            StmtResult res = runStmt(stmt);
            if (res != StmtResult.NONE){
                stexFrame.leafeDataFrame();
                return res;
            }
        }
        stexFrame.leafeDataFrame();
        return StmtResult.NONE;
    }

    private StmtResult runIfStmt(StexLifeGrammarParser.IfStmtContext ctx) {
        DataUnit decision = evalExpression(ctx.expression());
        if (decision.getType() != DataType.BOOL) {
            throw new InvalidTypeException(decision.getType(), DataType.BOOL);
        }
        if (decision.getBool()) {
            // run stmt
            StmtResult stmtResult =runBlock(ctx.block());
            if (stmtResult != StmtResult.NONE) {
                return stmtResult;
            }
        } else {
            boolean valid = false;
            for (StexLifeGrammarParser.ElseIfStmtContext elif: ctx.elseIfStmt()) {
                decision = evalExpression(elif.expression());
                if (decision.getType() != DataType.BOOL) {
                    throw new InvalidTypeException(decision.getType(), DataType.BOOL);
                }
                if (decision.getBool()) {
                    valid = true;
                    StmtResult stmtResult = runBlock(elif.block());
                    if (stmtResult != StmtResult.NONE) {
                        return stmtResult;
                    }
                    break;
                }
            }
            if (!valid && ctx.elseBlock() != null) {
                // run elseBlock.stmt
                StmtResult stmtResult = runBlock(ctx.elseBlock().block());
                if (stmtResult != StmtResult.NONE) {
                    return stmtResult;
                }
            }
        }
        return StmtResult.NONE;
    }

    private StmtResult runWhileStmt(StexLifeGrammarParser.WhileStmtContext ctx) {
        while (true) {
            DataUnit decision = evalExpression(ctx.expression());
            if (decision.getType() != DataType.BOOL) {
                throw new InvalidTypeException(decision.getType(), DataType.BOOL);
            }
            if (!decision.getBool()) {
                break;
            }
            StmtResult result = runBlock(ctx.block());
            if (result == StmtResult.RETURN) {
                return result;
            } else if (result == StmtResult.BREAK) {
                break;
            }
        }
        return StmtResult.NONE;
    }

    private StmtResult runForStmt(StexLifeGrammarParser.ForStmtContext ctx) {
        stexFrame.enterDataFrame();
        // Declare loop variable
        String name = ctx.ID().getText();
        if (stexFrame.getDataFrame().contains(name)) {
            throw new NameAlreadyDeclaredException(name);
        }
        DataUnit value = evalExpression(ctx.expression(0));
        stexFrame.getDataFrame().set(name, value);

        while (true) {
            // expression
            DataUnit decision = evalExpression(ctx.expression(1));
            if (decision.getType() != DataType.BOOL) {
                throw new InvalidTypeException(decision.getType(), DataType.BOOL);
            }
            if (!decision.getBool()) {
                break;
            }

            // stmt
            StmtResult result = runBlock(ctx.block());
            if (result == StmtResult.RETURN) {
                stexFrame.leafeDataFrame();
                return result;
            } else if (result == StmtResult.BREAK) {
                break;
            }

            // assign
            runAssignStmt(ctx.assignStmt());
        }
        stexFrame.leafeDataFrame();
        return StmtResult.NONE;
    }

    private StmtResult runForeachStmt(StexLifeGrammarParser.ForeachStmContext ctx) {
        DataUnit collection = evalExpression(ctx.expression());
        if (collection.getType() != DataType.ARRAY && collection.getType() != DataType.STRING && collection.getType() != DataType.OBJECT) {
            throw new InvalidTypeException(collection.getType(), DataType.ARRAY);
        }
        int len = collection.getType() == DataType.ARRAY ? collection.getArray().size() :
                collection.getType() == DataType.STRING ? collection.getString().length() :
                        collection.getObject().size();
        for (int i = 0; i < len; i++) {
            stexFrame.enterDataFrame();
            DataUnit elem = collection.getType() == DataType.ARRAY ? collection.getArray().get(i) :
                    collection.getType() == DataType.STRING ? new DataUnit(collection.getString().charAt(i) + "", DataType.STRING) :
                            new DataUnit(collection.getObject().entrySet().stream().sorted((a, b) -> a.getKey().compareTo(b.getKey())).skip(i).findFirst().get().getKey(), DataType.STRING);

            // declare loop variable
            String name = ctx.ID().getText();
            if (stexFrame.getDataFrame().contains(name)) {
                throw new NameAlreadyDeclaredException(name);
            }
            stexFrame.getDataFrame().set(name, elem);

            // stmt
            StmtResult result = runBlock(ctx.block());
            stexFrame.leafeDataFrame();

            if (result == StmtResult.RETURN) {
                return result;
            } else if (result == StmtResult.BREAK) {
                break;
            }
        }
        return StmtResult.NONE;
    }

    private StmtResult runTryStmt(StexLifeGrammarParser.TryStmtContext ctx) {
        StexFrame stack = stexFrame;
        DataFrame dataFrame = stexFrame.getDataFrame();
        StmtResult result;
        try {
            result = runBlock(ctx.block());
        } catch (StexLifeException e) {
            stexFrame = stack;
            while (stexFrame.getDataFrame() != dataFrame) {
                stexFrame.leafeDataFrame();
            }
            stexFrame.enterDataFrame();
            stexFrame.getDataFrame().set(ctx.catchBlock().ID().getText(), e.getException());
            result = runBlock(ctx.catchBlock().block());
            stexFrame.leafeDataFrame();
        }
        if (ctx.finallyBlock() != null) {
            StmtResult finallyResult = runBlock(ctx.finallyBlock().block());
            if (finallyResult != StmtResult.NONE) {
                result = finallyResult;
            }
        }
        return result;
    }

    private void runThrowStmt(StexLifeGrammarParser.ThrowStmtContext ctx) {
        DataUnit e = evalExpression(ctx.expression());
        if (e.getType() != DataType.OBJECT) {
            throw new InvalidTypeException(e.getType(), DataType.OBJECT);
        }
        if (!e.getObject().containsKey("msg")) {
            e.getObject().put("msg", new DataUnit("", DataType.STRING));
        }
        throw new StexLifeException(e.getObject());
    }

    private StmtResult runReturnStmt(StexLifeGrammarParser.ReturnStmtContext ctx) {
        if (ctx.expression() != null) {
            stexFrame.setResult(evalExpression(ctx.expression()));
        }
        return StmtResult.RETURN;
    }

    private DataUnit evalExpression(StexLifeGrammarParser.ExpressionContext ctx) {
        return evalExpression(new ExpressionWrapper(ctx));
    }

    private DataUnit evalOperationExpression(StexLifeGrammarParser.OperationExpressionContext ctx) {
        return evalExpression(new ExpressionWrapper(ctx));
    }

    private DataUnit evalShortAssignOperation(StexLifeGrammarParser.ShortAssignOpContext ctx, DataUnit left, DataUnit right) {
        if (ctx.ADD() != null) {
            return Arithmetics.Add(left, right);
        } else if (ctx.SUB() != null) {
            return Arithmetics.Sub(left, right);
        } else if (ctx.MUL() != null) {
            return Arithmetics.Mul(left, right);
        } else if (ctx.DIV() != null) {
            return Arithmetics.Div(left, right);
        } else if (ctx.MOD() != null) {
            return Arithmetics.Mod(left, right);
        }

        throw new StexLifeException("Invalid operation: " + ctx.getText());
    }

    private DataUnit evalExpression(ExpressionWrapper e) {
        if (e.expression() != null) {
            return evalExpression(e.expression());
        } else if (e.operand() != null) {
            return evalOperand(e.operand());
        } else if (e.array() != null) {
            return evalArray(e.array());
        } else if (e.object() != null) {
            return evalObject(e.object());
        } else if (e.dynamicAccess() != null) {
            return evalDynamicAccess(e.dynamicAccess());
        } else if (e.operation() != null) {
            return evalOperation(e.operation());
        } else if (e.ternaryExpression() != null) {
            return evalTernaryExpression(e.ternaryExpression());
        } else if (e.functionCall() != null) {
            return evalFunctionCall(e.functionCall());
        } else if (e.anonymousFunction() != null) {
            return new DataUnit(new FunctionWrapper(e.anonymousFunction(), stexFrame.getModule()), DataType.FUNCTION);
        } else if (e.getSelfFunctionRef() != null) {
            DataUnit fun = evalExpression(e.getSelfFunctionRef().expression());
            DataType.expecting(fun, DataType.FUNCTION);
            return fun;
        }

        throw new RuntimeException("Unexpected operation expression");
    }

    private DataUnit evalOperand(StexLifeGrammarParser.OperandContext ctx) {
        if (ctx.value() != null) {
            return Converter.fromValueContext(ctx.value());
        }

        // IDENTIFIER
        DataUnit dataUnit;
        dataUnit = resolveIdentifier(ctx.identifier());
        if (dataUnit.getType() == DataType.OBJECT || dataUnit.getType() == DataType.ARRAY ||
                dataUnit.getType() == DataType.FUNCTION || dataUnit.getType() == DataType.LIMITED) {
            return new DataUnit(dataUnit.getContent(), dataUnit.getType());
        }
        return dataUnit.copy();
    }

    private DataUnit evalArray(StexLifeGrammarParser.ArrayContext ctx) {
        List<DataUnit> arr = new ArrayList<>();
        for (int i = 0; i < ctx.expression().size(); i++) {
            arr.add(evalExpression(ctx.expression(i)));
        }
        return new DataUnit(arr, DataType.ARRAY);
    }

    private DataUnit evalObject(StexLifeGrammarParser.ObjectContext ctx) {
        Map<String, DataUnit> obj = new HashMap<>();
        DataUnit o = new DataUnit(obj, DataType.OBJECT);
        for (StexLifeGrammarParser.ObjectFieldContext field: ctx.objectField()) {
            String name = field.ID().getText();
            if (obj.containsKey(name)) {
                throw new NameAlreadyDeclaredException(name);
            }
            DataUnit value = evalExpression(field.expression());
            if (value.getType() == DataType.FUNCTION &&
                    (field.expression().anonymousFunction() != null || field.expression().selfFunctionRef() != null)) {
                value.getFunction().setCtx(o);
            }
            obj.put(name, value);
        }
        return o;
    }

    private DataUnit evalDynamicAccess(StexLifeGrammarParser.DynamicAccessContext ctx) {
        DataUnit arr = resolveIdentifier(ctx.identifier());
        DataUnit index = evalExpression(ctx.expression());
        if (arr.getType() == DataType.ARRAY && index.getType() == DataType.INT) {
            return arr.getArray().get(index.getInt().intValue());
        }

        if (arr.getType() == DataType.STRING && index.getType() == DataType.INT) {
            return new DataUnit(arr.getString().charAt(index.getInt().intValue()) + "", DataType.STRING);
        }

        if (arr.getType() == DataType.OBJECT && index.getType() == DataType.STRING) {
            return arr.getObject().get(index.getString());
        }

        throw new InvalidTypeException(index.getType(), DataType.INT);
    }

    private DataUnit evalOperation(StexLifeGrammarParser.OperationContext ctx) {
        if (ctx.notOperation() != null) {
            DataUnit exp = evalExpression(ctx.notOperation().expression());
            if (exp.getType() != DataType.BOOL) {
                throw new InvalidTypeException(exp.getType(), DataType.BOOL);
            }
            return new DataUnit(!exp.getBool().booleanValue(), DataType.BOOL);
        }

        DataUnit left = evalOperationExpression(ctx.operationExpression());
        if (ctx.operationType().AND() != null) {
            if (left.getType() != DataType.BOOL) {
                throw new InvalidTypeException(left.getType(), DataType.BOOL);
            }
            if (!left.getBool().booleanValue()) {
                return new DataUnit(false, DataType.BOOL);
            }
            DataUnit right = evalExpression(ctx.expression());
            if (right.getType() != DataType.BOOL) {
                throw new InvalidTypeException(right.getType(), DataType.BOOL);
            }
            return new DataUnit(right.getBool(), DataType.BOOL);
        } else if (ctx.operationType().OR() != null) {
            if (left.getType() != DataType.BOOL) {
                throw new InvalidTypeException(left.getType(), DataType.BOOL);
            }
            if (left.getBool().booleanValue()) {
                return new DataUnit(true, DataType.BOOL);
            }
            DataUnit right = evalExpression(ctx.expression());
            if (right.getType() != DataType.BOOL) {
                throw new InvalidTypeException(right.getType(), DataType.BOOL);
            }
            return new DataUnit(left.getBool() && right.getBool(), DataType.BOOL);
        }

        DataUnit right = evalExpression(ctx.expression());
        if (ctx.operationType().ADD() != null) {
            return Arithmetics.Add(left, right);
        } else if (ctx.operationType().SUB() != null) {
            return Arithmetics.Sub(left, right);
        } else if (ctx.operationType().MUL() != null) {
            return Arithmetics.Mul(left, right);
        } else if (ctx.operationType().DIV() != null) {
            return Arithmetics.Div(left, right);
        } else if (ctx.operationType().MOD() != null) {
            return Arithmetics.Mod(left, right);
        } else if (ctx.operationType().EQU() != null) {
            return new DataUnit(left.equals(right), DataType.BOOL);
        } else if (ctx.operationType().NEQ() != null) {
            return new DataUnit(!left.equals(right), DataType.BOOL);
        } else if (ctx.operationType().GRT() != null) {
            return new DataUnit(left.compareTo(right) > 0, DataType.BOOL);
        } else if (ctx.operationType().SMT() != null) {
            return new DataUnit(left.compareTo(right) < 0, DataType.BOOL);
        } else if (ctx.operationType().GRE() != null) {
            return new DataUnit(left.compareTo(right) >= 0, DataType.BOOL);
        } else if (ctx.operationType().SME() != null) {
            return new DataUnit(left.compareTo(right) <= 0, DataType.BOOL);
        } else if (ctx.operationType().IN() != null) {
            return Arithmetics.In(left, right);
        }

        throw new RuntimeException("Unexpected operator");
    }

    private DataUnit evalTernaryExpression(StexLifeGrammarParser.TernaryExpressionContext ctx) {
        DataUnit decision = evalExpression(ctx.expression(0));
        DataType.expecting(decision, DataType.BOOL);
        return evalExpression(ctx.expression(decision.getBool().booleanValue() ? 1 : 2));
    }

    private DataUnit evalFunctionCall(StexLifeGrammarParser.FunctionCallContext ctx) {
        DataUnit fun = resolveIdentifier(ctx.identifier());
        DataType.expecting(fun, DataType.FUNCTION);

        DataUnit[] args = new DataUnit[ctx.argList().expression().size()];
        for (int i = 0; i < args.length; i++) {
            args[i] = evalExpression(ctx.argList().expression(i));
        }

        return run(fun.getFunction(), args);
    }

}
