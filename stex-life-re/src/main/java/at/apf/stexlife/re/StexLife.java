package at.apf.stexlife.re;

import at.apf.stexlife.PluginRegistryImpl;
import at.apf.stexlife.VMImpl;
import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.StexLifeVM;
import at.apf.stexlife.commons.Commons;
import at.apf.stexlife.exception.UncaughtExceptionException;
import at.apf.stexlife.parser.StexLifeCodeParser;
import at.apf.stexlife.parser.antlr4.StexLifeGrammarParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class StexLife {

    public static void main(String[] args) {
        String sourceFile = args[0];
        PluginRegistryImpl pluginRegistry = new PluginRegistryImpl();
        pluginRegistry.register(Commons.commons());
        StexLifeCodeParser parser = new StexLifeCodeParser();
        String code;
        try {
            code = new String(Files.readAllBytes(Paths.get(sourceFile)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }
        StexLifeGrammarParser.ProgramContext program = parser.parse(code);
        StexLifeVM vm = new VMImpl(program, new File(sourceFile), pluginRegistry);
        try {
            List<DataUnit> programArgs = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                programArgs.add(new DataUnit(args[i], DataType.STRING));
            }
            vm.loadIncludes();
            vm.run("main", new DataUnit[]{ new DataUnit(programArgs, DataType.ARRAY) });
        } catch (UncaughtExceptionException e) {
            System.err.println(e.getCause().getMessage());
            System.exit(1);
            return;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }
    }
}
