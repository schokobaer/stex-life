package at.apf.stexlife;

import org.antlr.v4.runtime.ANTLRInputStream;
import at.apf.stexlife.parser.antlr4.StexLifeGrammarLexer;
import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Program {

    static String test1 = "main() {\n" +
            "    let a = 0;\n" +
            "    let b = \"asdf\";\n" +
            "    let c = true;\n" +
            "    let d = null;\n" +
            "    let e = 1.23;\n" +
            "    let f = [1, \"asdf\", true];\n" +
            "    let g = {id: 1, name: \"foo\"};\n" +
            "\n" +
            "    let h = 0;\n" +
            "}";

    public static void main(String[] args) {
        ANTLRInputStream inputStream = new ANTLRInputStream(test1);
        StexLifeGrammarLexer lexer = new StexLifeGrammarLexer(inputStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        StexLifeGrammarParser parser = new StexLifeGrammarParser(tokenStream);

        StexLifeGrammarParser.ProgramContext program = parser.program();

        VMImpl vm = new VMImpl(program);

        vm.run("main");
    }


}
