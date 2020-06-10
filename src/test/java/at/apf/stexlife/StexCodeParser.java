package at.apf.stexlife;

import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class StexCodeParser {
    public static StexLifeGrammarParser.ProgramContext parse(String code) {
        ANTLRInputStream inputStream = new ANTLRInputStream(code);
        at.apf.stexlife.parser.antlr4.StexLifeGrammarLexer lexer = new at.apf.stexlife.parser.antlr4.StexLifeGrammarLexer(inputStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        StexLifeGrammarParser parser = new StexLifeGrammarParser(tokenStream);
        return parser.program();
    }
}
