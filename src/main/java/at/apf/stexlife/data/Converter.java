package at.apf.stexlife.data;

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
        return new DataUnit(ctx.STRING(), DataType.STRING);
    }
}
