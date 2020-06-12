package at.apf.stexlife.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class StexLifeCodeParser {

    public at.apf.stexlife.parser.antlr4.StexLifeGrammarParser.ProgramContext parse(String code) {
        ANTLRInputStream inputStream = new ANTLRInputStream(code);
        at.apf.stexlife.parser.antlr4.StexLifeGrammarLexer lexer = new at.apf.stexlife.parser.antlr4.StexLifeGrammarLexer(inputStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        at.apf.stexlife.parser.antlr4.StexLifeGrammarParser parser = new at.apf.stexlife.parser.antlr4.StexLifeGrammarParser(tokenStream);
        return parser.program();
    }
}
