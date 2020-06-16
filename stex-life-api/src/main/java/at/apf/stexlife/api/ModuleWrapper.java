package at.apf.stexlife.api;

import at.apf.stexlife.api.exception.StexLifeException;
import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModuleWrapper {
    private StexLifeGrammarParser.ProgramContext program;
    private Path location;
    private Map<String, Pair<String, String>> includes = new HashMap<>();   // <alias, <module, fun>>

    public ModuleWrapper(StexLifeGrammarParser.ProgramContext program, Path location) {
        this.program = program;
        this.location = location;
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

    public Path getLocation() {
        return location;
    }
}
