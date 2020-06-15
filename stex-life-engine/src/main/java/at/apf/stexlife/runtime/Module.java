package at.apf.stexlife.runtime;

import at.apf.stexlife.api.exception.StexLifeException;
import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Module {
    private StexLifeGrammarParser.ProgramContext program;
    private Map<String, Pair<String, String>> includes = new HashMap<>();   // <alias, <module, fun>>

    public Module(StexLifeGrammarParser.ProgramContext program) {
        this.program = program;
    }

    public StexLifeGrammarParser.ProgramContext getProgram() {
        return program;
    }

    public Map<String, Pair<String, String>> getIncludes() {
        return includes;
    }

    public String getName() {
        if (program.module() == null) {
            throw new StexLifeException("Imported module has no name");
        }
        return program.module().identifier().getText();
    }

    public List<String> getExportedFunctions() {
        return program.function().stream()
                .filter(f -> f.EXPORT() != null)
                .map(f -> f.ID().getText())
                .collect(Collectors.toList());
    }
}
