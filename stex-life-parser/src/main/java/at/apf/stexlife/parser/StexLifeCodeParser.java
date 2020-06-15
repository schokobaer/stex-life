package at.apf.stexlife.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StexLifeCodeParser {

    public at.apf.stexlife.parser.antlr4.StexLifeGrammarParser.ProgramContext parse(String code) {
        ANTLRInputStream inputStream = new ANTLRInputStream(code);
        at.apf.stexlife.parser.antlr4.StexLifeGrammarLexer lexer = new at.apf.stexlife.parser.antlr4.StexLifeGrammarLexer(inputStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        at.apf.stexlife.parser.antlr4.StexLifeGrammarParser parser = new at.apf.stexlife.parser.antlr4.StexLifeGrammarParser(tokenStream);
        return parser.program();
    }

    public at.apf.stexlife.parser.antlr4.StexLifeGrammarParser.ProgramContext parse(File file) throws IOException {
        String code = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
        return parse(code);
    }
}
