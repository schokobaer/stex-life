package at.apf.stexlife;

import at.apf.stexlife.data.Converter;
import at.apf.stexlife.data.DataType;
import at.apf.stexlife.data.DataUnit;
import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;
import at.apf.stexlife.runtime.DataFrame;
import at.apf.stexlife.runtime.StaxFrame;
import at.apf.stexlife.runtime.exception.NameAlreadyDeclaredException;
import at.apf.stexlife.runtime.exception.NameNotFoundException;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Optional;

public class VMImpl {

    private StexLifeGrammarParser.ProgramContext program;
    private StaxFrame staxFrame;

    public VMImpl(StexLifeGrammarParser.ProgramContext program) {
        this.program = program;
    }

    public void run(String function) {
        this.run(function, new DataUnit[0]);
    }

    public void run(String function, DataUnit[] data) {
        Optional<StexLifeGrammarParser.FunctionContext> fun = program.functionlist().function().stream()
                .filter(f -> f.ID().getText().equals(function) && f.paramlist().ID().size() == data.length)
                .findFirst();
        if (fun.isPresent()) {
            staxFrame = new StaxFrame(null, new DataFrame(null));
            for (int i = 0; i < fun.get().paramlist().ID().size(); i++) {
                staxFrame.getDataFrame().set(fun.get().paramlist().ID(i).getText(), data[i]);
            }
            runFunction(fun.get());
        } else {
            throw new NameNotFoundException(function + "(" + data.length + ")");
        }
    }

    private void runFunction(StexLifeGrammarParser.FunctionContext f) {
        for (StexLifeGrammarParser.StmtContext stmt: f.stmt()) {
            if (stmt.declareStmt() != null) {
                StexLifeGrammarParser.DeclareStmtContext dcl = stmt.declareStmt();
                String name = dcl.ID().getText();
                if (staxFrame.getDataFrame().contains(name)) {
                    throw new NameAlreadyDeclaredException(name);
                }
                DataUnit value = evalExpression(dcl.expression());
                staxFrame.getDataFrame().set(name, value);
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
                List<TerminalNode> ids = e.operand().identifier().ID();
                DataUnit dataUnit = staxFrame.getDataFrame().get(ids.get(0).getText());
                for (int i = 1; i < ids.size(); i++) {
                    String name = ids.get(i).getText();
                    if (dataUnit.getType() != DataType.OBJECT || !dataUnit.getObject().containsKey(name)) {
                        throw new NameNotFoundException(name);
                    }
                    dataUnit = dataUnit.getObject().get(name);
                }
                if (dataUnit.getType() == DataType.OBJECT || dataUnit.getType() == DataType.ARRAY) {
                    return new DataUnit(dataUnit.getContent(), dataUnit.getType());
                }
                return new DataUnit(dataUnit.copy(), dataUnit.getType());
            }
        }

        return new DataUnit(null, DataType.NULL);
    }
}
