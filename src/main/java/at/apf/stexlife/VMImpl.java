package at.apf.stexlife;

import at.apf.stexlife.data.Converter;
import at.apf.stexlife.data.DataType;
import at.apf.stexlife.data.DataUnit;
import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;
import at.apf.stexlife.runtime.Arithmetics;
import at.apf.stexlife.runtime.DataFrame;
import at.apf.stexlife.runtime.StexFrame;
import at.apf.stexlife.runtime.exception.InvalidTypeException;
import at.apf.stexlife.runtime.exception.NameAlreadyDeclaredException;
import at.apf.stexlife.runtime.exception.NameNotFoundException;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
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
            runStmt(stmt);
        }
    }

    private void runStmt(StexLifeGrammarParser.StmtContext stmt) {
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
                arr.getArray().set(index.getInt().intValue(), value);
            }
        }  else if (stmt.ifStmt() != null) {
            DataUnit decision = evalExpression(stmt.ifStmt().expression());
            if (decision.getType() != DataType.BOOL) {
                throw new InvalidTypeException(decision.getType(), DataType.BOOL);
            }
            if (decision.getBool()) {
                // run stmt
                stexFrame.enterDataFrame();
                for (StexLifeGrammarParser.StmtContext ifStmt: stmt.ifStmt().stmt()) {
                    runStmt(ifStmt);
                }
                stexFrame.leafeDataFrame();
            } else {
                boolean valid = false;
                for (StexLifeGrammarParser.ElseIfStmtContext elif: stmt.ifStmt().elseIfStmt()) {
                    decision = evalExpression(elif.expression());
                    if (decision.getType() != DataType.BOOL) {
                        throw new InvalidTypeException(decision.getType(), DataType.BOOL);
                    }
                    if (decision.getBool()) {
                        valid = true;
                        stexFrame.enterDataFrame();
                        for (StexLifeGrammarParser.StmtContext elifStmt: elif.stmt()) {
                            runStmt(elifStmt);
                        }
                        stexFrame.leafeDataFrame();
                        break;
                    }
                }
                if (!valid && stmt.ifStmt().elseBlock() != null) {
                    // run elseBlock.stmt
                    stexFrame.enterDataFrame();
                    for (StexLifeGrammarParser.StmtContext elStmt: stmt.ifStmt().elseBlock().stmt()) {
                        runStmt(elStmt);
                    }
                    stexFrame.leafeDataFrame();
                }
            }
        } else if (stmt.returnStmt() != null) {
            StexLifeGrammarParser.ReturnStmtContext ctx = stmt.returnStmt();
            if (ctx.expression() != null) {
                stexFrame.setResult(evalExpression(ctx.expression()));
            }
            return;
        }
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
        } else if (e.arrayAccess() != null) {
            return evalArrayAccess(e.arrayAccess());
        } else if (e.operation() != null) {
            return evalOperation(e.operation());
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
        } else if (e.arrayAccess() != null) {
            return evalArrayAccess(e.arrayAccess());
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
            if (program.functionlist().function().stream()
                    .anyMatch(f -> f.ID().getText().equals(ctx.identifier().ID(0).getText()))) {
                dataUnit = new DataUnit(ctx.identifier().ID(0).getText(), DataType.FUNCTION);
            }
            else throw ex;
        }
        if (dataUnit.getType() == DataType.OBJECT || dataUnit.getType() == DataType.ARRAY) {
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

    private DataUnit evalArrayAccess(StexLifeGrammarParser.ArrayAccessContext ctx) {
        DataUnit index = evalExpression(ctx.expression());
        if (index.getType() != DataType.INT) {
            throw new InvalidTypeException(index.getType(), DataType.INT);
        }
        DataUnit arr = resolveIdentifier(ctx.identifier());
        if (arr.getType() != DataType.ARRAY) {
            throw new InvalidTypeException(arr.getType(), DataType.ARRAY);
        }
        return arr.getArray().get(index.getInt().intValue());
    }

    private DataUnit evalOperation(StexLifeGrammarParser.OperationContext ctx) {
        if (ctx.notoperation() != null) {
            DataUnit exp = evalExpression(ctx.notoperation().expression());
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
}
