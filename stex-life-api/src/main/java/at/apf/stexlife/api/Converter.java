package at.apf.stexlife.api;

import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;

public class Converter {

    public static DataUnit fromValueContext(StexLifeGrammarParser.ValueContext ctx) {
        if (ctx.BOOLEAN() != null) {
            return new DataUnit(ctx.BOOLEAN().getText().equals("true"), DataType.BOOL);
        } else if (ctx.NULL() != null) {
            return new DataUnit(null, DataType.NULL);
        } else if (ctx.INT() != null) {
            return new DataUnit(Long.parseLong(ctx.INT().getText()), DataType.INT);
        } else if (ctx.FLOAT() != null) {
            return new DataUnit(Double.parseDouble(ctx.FLOAT().getText()), DataType.FLOAT);
        }
        return new DataUnit(ctx.STRING().getText().substring(1, ctx.STRING().getText().length() - 1), DataType.STRING);
    }

    public static String stringify(DataUnit data) {
        if (data.getType() == DataType.ARRAY) {
            return "[" + data.getArray().stream()
                    .map(e -> stringify(e))
                    .reduce((acc, str) -> acc + ", " + str) + "]";
        } else if (data.getType() == DataType.OBJECT) {
            return "{" + data.getObject().entrySet().stream()
                    .map(kv -> kv.getKey() + ": " + stringify(kv.getValue()))
                    .reduce((acc, str) -> acc + ", " + str) + "}";
        } else if (data.getType() == DataType.FUNCTION) {
            return data.getFunction().getName() + "{" + data.getFunction().getParamList().ID().size() + "}";
        }
        return data.getContent() != null ? data.getContent().toString() : "null";
    }
}
