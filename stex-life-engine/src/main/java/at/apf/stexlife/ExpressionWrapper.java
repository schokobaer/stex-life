package at.apf.stexlife;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.FunctionWrapper;
import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;

public class ExpressionWrapper {
    private StexLifeGrammarParser.ExpressionContext expression;
    private StexLifeGrammarParser.OperationContext operation;
    private StexLifeGrammarParser.OperandContext operand;
    private StexLifeGrammarParser.TernaryExpressionContext ternaryExpression;
    private StexLifeGrammarParser.DynamicAccessContext dynamicAccess;
    private StexLifeGrammarParser.ArrayContext array;
    private StexLifeGrammarParser.ObjectContext object;
    private StexLifeGrammarParser.FunctionCallContext functionCall;
    private StexLifeGrammarParser.AnonymousFunctionContext anonymousFunction;
    private StexLifeGrammarParser.SelfFunctionRefContext selfFunctionRef;

    public ExpressionWrapper(StexLifeGrammarParser.ExpressionContext e) {
        expression = e.expression();
        operation = e.operation();
        operand = e.operand();
        ternaryExpression = e.ternaryExpression();
        dynamicAccess = e.dynamicAccess();
        array = e.array();
        object = e.object();
        functionCall = e.functionCall();
        anonymousFunction = e.anonymousFunction();
        selfFunctionRef = e.selfFunctionRef();
    }

    public ExpressionWrapper(StexLifeGrammarParser.OperationExpressionContext e) {
        expression = e.expression();
        operand = e.operand();
        ternaryExpression = e.ternaryExpression();
        dynamicAccess = e.dynamicAccess();
        array = e.array();
        object = e.object();
        functionCall = e.functionCall();
    }

    public StexLifeGrammarParser.AnonymousFunctionContext anonymousFunction() {
        return anonymousFunction;
    }

    public StexLifeGrammarParser.ArrayContext array() {
        return array;
    }

    public StexLifeGrammarParser.DynamicAccessContext dynamicAccess() {
        return dynamicAccess;
    }

    public StexLifeGrammarParser.ExpressionContext expression() {
        return expression;
    }

    public StexLifeGrammarParser.FunctionCallContext functionCall() {
        return functionCall;
    }

    public StexLifeGrammarParser.ObjectContext object() {
        return object;
    }

    public StexLifeGrammarParser.OperandContext operand() {
        return operand;
    }

    public StexLifeGrammarParser.OperationContext operation() {
        return operation;
    }

    public StexLifeGrammarParser.TernaryExpressionContext ternaryExpression() {
        return ternaryExpression;
    }

    public StexLifeGrammarParser.SelfFunctionRefContext getSelfFunctionRef() {
        return selfFunctionRef;
    }
}
