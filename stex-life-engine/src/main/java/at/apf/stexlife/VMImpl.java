package at.apf.stexlife;

import at.apf.stexlife.api.Converter;
import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.FunctionWrapper;
import at.apf.stexlife.api.StexLifeVM;
import at.apf.stexlife.api.exception.InvalidTypeException;
import at.apf.stexlife.api.exception.StexLifeException;
import at.apf.stexlife.exception.UncaughtExceptionException;
import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;
import at.apf.stexlife.runtime.Arithmetics;
import at.apf.stexlife.runtime.DataFrame;
import at.apf.stexlife.runtime.StexFrame;
import at.apf.stexlife.runtime.exception.NameAlreadyDeclaredException;
import at.apf.stexlife.runtime.exception.NameNotFoundException;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VMImpl implements StexLifeVM {

    private StexLifeGrammarParser.ProgramContext program;
    private StexFrame stexFrame;
    private PluginRegistry pluginRegistry;
    private Map<String, Pair<String, String>> includes = new HashMap<>(); // <alias, <module, function>>

    StexFrame getStexFrame() {
        return stexFrame;
    }

    public VMImpl(StexLifeGrammarParser.ProgramContext program) {
        this.program = program;
        this.pluginRegistry = new PluginRegistryImpl();
    }

    public VMImpl(StexLifeGrammarParser.ProgramContext program, PluginRegistry pluginRegistry) {
        this.program = program;
        this.pluginRegistry = pluginRegistry;
    }

    @Override
    public DataUnit run(String function) throws UncaughtExceptionException {
        return this.run(function, new DataUnit[0]);
    }

    @Override
    public DataUnit run(String function, DataUnit[] data) throws UncaughtExceptionException {
        Optional<StexLifeGrammarParser.FunctionContext> fun = program.function().stream()
                .filter(f -> f.ID().getText().equals(function) && f.paramList().ID().size() == data.length)
                .findFirst();
        if (fun.isPresent()) {
            stexFrame = new StexFrame(null, new DataFrame(null));
            for (int i = 0; i < fun.get().paramList().ID().size(); i++) {
                stexFrame.getDataFrame().set(fun.get().paramList().ID(i).getText(), data[i]);
            }
            try {
                return runFunction(new FunctionWrapper(fun.get()));
            } catch (StexLifeException e) {
                throw new UncaughtExceptionException(e);
            }
        } else {
            throw new UncaughtExceptionException(new NameNotFoundException(function + "{" + data.length + "}"));
        }
    }

    @Override
    public DataUnit run(FunctionWrapper function, DataUnit[] args) {
        // TODO: if function is anonymous or function context: Create new Stack, set args, call it
        // TODO: Else use plugin registry
        FunctionWrapper fw = function;
        if (function.isNamed()) {
            if (includes.containsKey(function.getName()) &&
                    pluginRegistry.isRegistered(includes.get(function.getName()).getLeft(),
                            includes.get(function.getName()).getRight(), args.length)) {
                return pluginRegistry.call(this, includes.get(function.getName()).getLeft(),
                        includes.get(function.getName()).getRight(), args);
            }

            Optional<StexLifeGrammarParser.FunctionContext> op = program.function().stream()
                    .filter(f -> f.ID().getText().equals(function.getName())
                            && f.paramList().ID().size() == args.length).findAny();
            if (op.isPresent()) {
                fw = new FunctionWrapper(op.get());
            } else {
                throw new NameNotFoundException(function.getName());
            }
        }

        DataFrame df = new DataFrame(null);
        for (int i = 0; i < args.length; i++) {
            df.set(fw.getParamList().ID(i).getText(), args[i]);
        }

        stexFrame = new StexFrame(stexFrame, df);
        DataUnit result = runFunction(fw);
        stexFrame = stexFrame.getParent();

        return result;
    }

    @Override
    public void loadIncludes() {
        program.include().forEach(i -> {
            i.includeDeclaration().forEach(dec -> {
                String usedName = dec.ID().size() == 2 ? dec.ID(1).getText() : dec.ID(0).getText();
                if (includes.containsKey(usedName)) {
                    throw new NameAlreadyDeclaredException(usedName);
                }
                includes.put(usedName, new ImmutablePair<>(i.includeSource().ID().getText(), dec.ID(0).getText()));
            });
        });
    }

    private DataUnit resolveIdentifier(StexLifeGrammarParser.IdentifierContext ctx) {
        List<TerminalNode> ids = ctx.ID();
        DataUnit dataUnit;
        int start = 0;
        if (ctx.SELF() != null) {
            if (stexFrame.getSelf() == null) {
                throw new NameNotFoundException("self");
            }
            dataUnit = stexFrame.getSelf();
        } else {
            dataUnit = stexFrame.getDataFrame().get(ids.get(0).getText());
            start = 1;
        }

        for (int i = start; i < ids.size(); i++) {
            String name = ids.get(i).getText();
            if (dataUnit.getType() != DataType.OBJECT || !dataUnit.getObject().containsKey(name)) {
                throw new NameNotFoundException(name);
            }
            dataUnit = dataUnit.getObject().get(name);
        }
        return dataUnit;
    }

    private DataUnit runFunction(FunctionWrapper f) {
        for (StexLifeGrammarParser.StmtContext stmt: f.getStmt()) {
            if (!runStmt(stmt)) {
                return stexFrame.getResult();
            }
        }
        return stexFrame.getResult();
    }

    private boolean runStmt(StexLifeGrammarParser.StmtContext stmt) {
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
            return true;
        } else if (stmt.tryStmt() != null) {
            return runTryStmt(stmt.tryStmt());
        } else if (stmt.throwStmt() != null) {
            runThrowStmt(stmt.throwStmt());
        } else if (stmt.returnStmt() != null) {
            return runReturnStmt(stmt.returnStmt());
        }
        return true;
    }

    private boolean runDeclareStmt(StexLifeGrammarParser.DeclareStmtContext ctx) {
        String name = ctx.ID().getText();
        if (stexFrame.getDataFrame().contains(name)) {
            throw new NameAlreadyDeclaredException(name);
        }
        DataUnit value = evalExpression(ctx.expression());
        stexFrame.getDataFrame().set(name, value);
        return true;
    }

    private boolean runAssignStmt(StexLifeGrammarParser.AssignStmtContext ctx) {
        DataUnit value = evalExpression(ctx.expression());
        if (ctx.assignee().identifier() != null) {
            // override the old value
            List<TerminalNode> ids = ctx.assignee().identifier().ID();
            if (ids.size() == 1) {
                stexFrame.getDataFrame().set(ids.get(0).getText(), value);
            } else {
                Map<String, DataUnit> obj = stexFrame.getDataFrame().get(ids.get(0).getText()).getObject();
                for (int i = 1; i < ids.size() - 1; i++) {
                    obj = obj.get(ids.get(i).getText()).getObject();
                }
                obj.put(ids.get(ids.size() - 1).getText(), value);
            }
        } else {
            DataUnit arr = resolveIdentifier(ctx.assignee().dynamicAccess().identifier());
            if (arr.getType() != DataType.ARRAY) {
                throw new InvalidTypeException(arr.getType(), DataType.ARRAY);
            }
            DataUnit index = evalExpression(ctx.assignee().dynamicAccess().expression());
            if (index.getType() != DataType.INT) {
                throw new InvalidTypeException(index.getType(), DataType.INT);
            }
            arr.getArray().set(index.getInt().intValue(), value);
        }
        return true;
    }

    private boolean runBlock(StexLifeGrammarParser.BlockContext ctx) {
        stexFrame.enterDataFrame();
        for (StexLifeGrammarParser.StmtContext stmt: ctx.stmt()) {
            if (!runStmt(stmt)) {
                stexFrame.leafeDataFrame();
                return false;
            }
        }
        stexFrame.leafeDataFrame();
        return true;
    }

    private boolean runIfStmt(StexLifeGrammarParser.IfStmtContext ctx) {
        DataUnit decision = evalExpression(ctx.expression());
        if (decision.getType() != DataType.BOOL) {
            throw new InvalidTypeException(decision.getType(), DataType.BOOL);
        }
        if (decision.getBool()) {
            // run stmt
            if (!runBlock(ctx.block())) {
                return false;
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
                    if (!runBlock(elif.block())) {
                        return false;
                    }
                    break;
                }
            }
            if (!valid && ctx.elseBlock() != null) {
                // run elseBlock.stmt
                if (!runBlock(ctx.elseBlock().block())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean runWhileStmt(StexLifeGrammarParser.WhileStmtContext ctx) {
        while (true) {
            DataUnit decision = evalExpression(ctx.expression());
            if (decision.getType() != DataType.BOOL) {
                throw new InvalidTypeException(decision.getType(), DataType.BOOL);
            }
            if (!decision.getBool()) {
                break;
            }
            if (!runBlock(ctx.block())) {
                return false;
            }
        }
        return true;
    }

    private boolean runForStmt(StexLifeGrammarParser.ForStmtContext ctx) {
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
            if (!runBlock(ctx.block())) {
                stexFrame.leafeDataFrame();
                return false;
            }

            // assign
            runAssignStmt(ctx.assignStmt());
        }
        stexFrame.leafeDataFrame();
        return true;
    }

    private boolean runForeachStmt(StexLifeGrammarParser.ForeachStmContext ctx) {
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
            if (!runBlock(ctx.block())) {
                stexFrame.leafeDataFrame();
                return false;
            }
            stexFrame.leafeDataFrame();
        }
        return true;
    }

    private boolean runTryStmt(StexLifeGrammarParser.TryStmtContext ctx) {
        StexFrame stack = stexFrame;
        DataFrame dataFrame = stexFrame.getDataFrame();
        boolean result;
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
            boolean finallyResult = runBlock(ctx.finallyBlock().block());
            result = result && finallyResult;
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

    private boolean runReturnStmt(StexLifeGrammarParser.ReturnStmtContext ctx) {
        if (ctx.expression() != null) {
            stexFrame.setResult(evalExpression(ctx.expression()));
        }
        return false;
    }

    private DataUnit evalExpression(StexLifeGrammarParser.ExpressionContext e) {
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
        } else if (e.functionCall() != null) {
            return evalFunctionCall(e.functionCall());
        } else if (e.anonymousFunction() != null) {
            return new DataUnit(new FunctionWrapper(e.anonymousFunction()), DataType.FUNCTION);
        }

        throw new RuntimeException("Unexpected operation expression");
    }

    private DataUnit evalOperationExpression(StexLifeGrammarParser.OperationExpressionContext e) {
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
        }

        throw new RuntimeException("Unexpected operation expression");
    }

    private DataUnit evalOperand(StexLifeGrammarParser.OperandContext ctx) {
        if (ctx.value() != null) {
            return Converter.fromValueContext(ctx.value());
        }
        // IDENTIFIER
        DataUnit dataUnit;
        try {
            dataUnit = resolveIdentifier(ctx.identifier());
        } catch (NameNotFoundException ex) {
            // local function: no self context
            String name = ctx.identifier().ID(0).getText();
            if (program.function().stream()
                    .anyMatch(f -> f.ID().getText().equals(name))) {
                dataUnit = new DataUnit(new FunctionWrapper(name), DataType.FUNCTION);
            }
            else throw ex;
        }
        if (dataUnit.getType() == DataType.OBJECT || dataUnit.getType() == DataType.ARRAY || dataUnit.getType() == DataType.FUNCTION) {
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
        for (StexLifeGrammarParser.ObjectFieldContext field: ctx.objectField()) {
            String name = field.ID().getText();
            if (obj.containsKey(name)) {
                throw new NameAlreadyDeclaredException(name);
            }
            obj.put(name, evalExpression(field.expression()));
        }
        return new DataUnit(obj, DataType.OBJECT);
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

    private DataUnit evalFunctionCall(StexLifeGrammarParser.FunctionCallContext ctx) {
        /**
         * 1. Look for name in dataFrame
         * 2. Look for name in program.functionlist
         * 3. Look for name in PluginRegistry
         */

        FunctionWrapper fw;
        int argListSize = ctx.argList().expression().size();

        try {
            // Function in variable
            DataUnit duf = resolveIdentifier(ctx.identifier());
            if (duf.getType() != DataType.FUNCTION) {
                throw new InvalidTypeException(duf.getType(), DataType.FUNCTION);
            }
            fw = duf.getFunction();
        } catch (NameNotFoundException e) {
            // local or imported function
            String name = ctx.identifier().ID().stream().map(tn -> tn.getText()).collect(Collectors.joining("."));
            Optional<StexLifeGrammarParser.FunctionContext> op = program.function().stream()
                    .filter(f -> f.ID().getText().equals(name) && f.paramList().ID().size() == argListSize)
                    .findFirst();
            if (op.isPresent()) {
                // local function
                fw = new FunctionWrapper(op.get());
            } else {
                // Look in PluginRegistry
                if (!includes.containsKey(name) ||
                        !pluginRegistry.isRegistered(includes.get(name).getLeft(), includes.get(name).getRight(), ctx.argList().expression().size())) {
                    throw new NameNotFoundException(name + "{" + ctx.argList().expression().size() + "}");
                }
                fw = new FunctionWrapper(name);
            }
        }

        // eval args
        DataUnit[] args = new DataUnit[argListSize];
        for (int i = 0; i < args.length; i++) {
            args[i] = evalExpression(ctx.argList().expression(i));
        }

        // if named
        if (fw.isNamed()) {
            // find local function
            String name = fw.getName();
            Optional<StexLifeGrammarParser.FunctionContext> op = program.function().stream()
                    .filter(f -> f.ID().getText().equals(name) && f.paramList().ID().size() == argListSize)
                    .findFirst();
            if (op.isPresent()) {
                fw = new FunctionWrapper(op.get());
            } else {
                // find plugin function
                if (!includes.containsKey(name) ||
                        !pluginRegistry.isRegistered(includes.get(name).getLeft(), includes.get(name).getRight(), ctx.argList().expression().size())) {
                    throw new NameNotFoundException(name);
                }
                return pluginRegistry.call(this, includes.get(name).getLeft(), includes.get(name).getRight(), args);
            }
        }

        DataFrame df = new DataFrame(null);
        for (int i = 0; i < args.length; i++) {
            df.set(fw.getParamList().ID(i).getText(), args[i]);
        }

        // Find context of invocation
        DataUnit self = null;
        if (ctx.identifier().SELF() != null && ctx.identifier().ID().size() > 0 || ctx.identifier().ID().size() > 1) {
            List<TerminalNode> ids = ctx.identifier().ID();
            int start = 0;
            if (ctx.identifier().SELF() != null) {
                self = stexFrame.getSelf();
            } else {
                self = stexFrame.getDataFrame().get(ids.get(0).getText());
                start = 1;
            }
            for (int i = start; i < ids.size() - 1; i++) {
                String name = ids.get(i).getText();
                self = self.getObject().get(name);
            }
        }

        // call
        stexFrame = new StexFrame(stexFrame, df);
        stexFrame.setSelf(self);
        DataUnit result = runFunction(fw);
        stexFrame = stexFrame.getParent();

        return result;
    }

}
