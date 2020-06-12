package at.apf.stexlife.parser;

import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;
import org.junit.Test;

public class ParserTest {

    private StexLifeCodeParser parser = new StexLifeCodeParser();

    @Test
    public void objectAccess() {
        String code =
                "main() {" +
                        "  let p = person();" +
                        "  return p.hello();" +
                        "}" +
                        "person() {" +
                        "  return {" +
                        "    hello: () { return \"foo\"; }" +
                        "  };" +
                        "}";
        StexLifeGrammarParser.ProgramContext p = parser.parse(code);
    }

    @Test
    public void doubleObjectAccess() {
        String code =
                "main() {" +
                        "  let p = person();" +
                        "  return p.a.b;" +
                        "}" +
                        "person() {" +
                        "  return {" +
                        "    a: {" +
                        "      b: 1" +
                        "    }" +
                        "  };" +
                        "}";
        StexLifeGrammarParser.ProgramContext p = parser.parse(code);
    }

    //@Test
    public void objectAccessDirectlyFromFunctionResult() {
        String code =
                "main() {" +
                        "  let p = person().a;" +
                        "}" +
                        "person() {" +
                        "  return {" +
                        "    a: {" +
                        "      b: 1" +
                        "    }" +
                        "  };" +
                        "}";
        StexLifeGrammarParser.ProgramContext p = parser.parse(code);
    }
}
