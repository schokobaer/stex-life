package at.apf.stexlife;

import at.apf.stexlife.data.Converter;
import at.apf.stexlife.data.DataType;
import at.apf.stexlife.data.DataUnit;
import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;
import at.apf.stexlife.runtime.DataFrame;
import at.apf.stexlife.runtime.StexFrame;
import at.apf.stexlife.runtime.exception.InvalidTypeException;
import at.apf.stexlife.runtime.exception.NameAlreadyDeclaredException;
import at.apf.stexlife.runtime.exception.NameNotFoundException;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VMImpl {

    private StexLifeGrammarParser.ProgramContext program;
    private StexFrame stexFrame;

    StexFrame getStexFrame() {
        return stexFrame;
    }

    public VMImpl(StexLifeGrammarParser.ProgramContext program) {
        this.program = program;
    }

    public DataUnit run(String function) {
        return this.run(function, new DataUnit[0]);
    }

    public DataUnit run(String function, DataUnit[] data) {
        Optional<StexLifeGrammarParser.FunctionContext> fun = program.functionlist().function().stream()
                .filter(f -> f.ID().getText().equals(function) && f.paramlist().ID().size() == data.length)
                .findFirst();
        if (fun.isPresent()) {
            stexFrame = new StexFrame(null, new DataFrame(null));
            for (int i = 0; i < fun.get().paramlist().ID().size(); i++) {
                stexFrame.getDataFrame().set(fun.get().paramlist().ID(i).getText(), data[i]);
            }
            runFunction(fun.get());
            return stexFrame.getResult();
        } else {
            throw new NameNotFoundException(function + "(" + data.length + ")");
        }
    }

    private DataUnit resolveIdentifier(StexLifeGrammarParser.IdentifierContext ctx) {
        List<TerminalNode> ids = ctx.ID();
        DataUnit dataUnit = stexFrame.getDataFrame().get(ids.get(0).getText());
        for (int i = 1; i < ids.size(); i++) {
            String name = ids.get(i).getText();
            if (dataUnit.getType() != DataType.OBJECT || !dataUnit.getObject().containsKey(name)) {
                throw new NameNotFoundException(name);
            }
            dataUnit = dataUnit.getObject().get(name);
        }
        return dataUnit;
    }

    private void runFunction(StexLifeGrammarParser.FunctionContext f) {
        for (StexLifeGrammarParser.StmtContext stmt: f.stmt()) {
            if (stmt.declareStmt() != null) {
                StexLifeGrammarParser.DeclareStmtContext dcl = stmt.declareStmt();
                String name = dcl.ID().getText();
                if (stexFrame.getDataFrame().contains(name)) {
                    throw new NameAlreadyDeclaredException(name);
                }
                DataUnit value = evalExpression(dcl.expression());
                stexFrame.getDataFrame().set(name, value);
            } else if (stmt.assignStmt() != null) {
                StexLifeGrammarParser.AssignStmtContext ctx = stmt.assignStmt();
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
                    DataUnit arr = resolveIdentifier(ctx.assignee().arrayAccess().identifier());
                    if (arr.getType() != DataType.ARRAY) {
                        throw new InvalidTypeException(arr.getType(), DataType.ARRAY);
                    }
                    DataUnit index = evalExpression(ctx.assignee().arrayAccess().expression());
                    if (index.getType() != DataType.INT) {
                        throw new InvalidTypeException(index.getType(), DataType.INT);
                    }
                    arr.getArray()[index.getInt().intValue()] = value;
                }
            } else if (stmt.returnStmt() != null) {
                StexLifeGrammarParser.ReturnStmtContext ctx = stmt.returnStmt();
                if (ctx.expression() != null) {
                    stexFrame.setResult(evalExpression(ctx.expression()));
                }
                return;
            }

        }
    }

    private DataUnit evalExpression(StexLifeGrammarParser.ExpressionContext e) {
        if (e.expression() != null) {
            return evalExpression(e.expression());
        } else if (e.operand() != null) {
            if (e.operand().value() != null) {
                return Converter.fromValueContext(e.operand().value());
            } else {
                // IDENTIFIER
                DataUnit dataUnit;
                try {
                    dataUnit = resolveIdentifier(e.operand().identifier());
                } catch (NameNotFoundException ex) {
                    if (program.functionlist().function().stream()
                            .anyMatch(f -> f.ID().getText().equals(e.operand().identifier().ID(0).getText()))) {
                        dataUnit = new DataUnit(e.operand().identifier().ID(0).getText(), DataType.FUNCTION);
                    }
                    else throw ex;
                }
                if (dataUnit.getType() == DataType.OBJECT || dataUnit.getType() == DataType.ARRAY) {
                    return new DataUnit(dataUnit.getContent(), dataUnit.getType());
                }
                return new DataUnit(dataUnit.copy(), dataUnit.getType());
            }
        } else if (e.array() != null) {
            StexLifeGrammarParser.ArrayContext ctx = e.array();
            DataUnit[] arr = new DataUnit[ctx.expression().size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = evalExpression(ctx.expression(i));
            }
            return new DataUnit(arr, DataType.ARRAY);
        } else if (e.object() != null) {
            StexLifeGrammarParser.ObjectContext ctx = e.object();
            Map<String, DataUnit> obj = new HashMap<>();
            for (StexLifeGrammarParser.ObjectFieldContext field: ctx.objectField()) {
                String name = field.ID().getText();
                if (obj.containsKey(name)) {
                    throw new NameAlreadyDeclaredException(name);
                }
                obj.put(name, evalExpression(field.expression()));
            }
            return new DataUnit(obj, DataType.OBJECT);
        } else if (e.arrayAccess() != null) {
            StexLifeGrammarParser.ArrayAccessContext ctx = e.arrayAccess();
            DataUnit index = evalExpression(ctx.expression());
            if (index.getType() != DataType.INT) {
                throw new InvalidTypeException(index.getType(), DataType.INT);
            }
            DataUnit arr = resolveIdentifier(ctx.identifier());
            if (arr.getType() != DataType.ARRAY) {
                throw new InvalidTypeException(arr.getType(), DataType.ARRAY);
            }
            return arr.getArray()[index.getInt().intValue()];
        }

        return new DataUnit(null, DataType.NULL);
    }
}
