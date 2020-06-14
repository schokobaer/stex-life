package at.apf.stexlife.re;

import at.apf.stexlife.PluginRegistryImpl;
import at.apf.stexlife.VMImpl;
import at.apf.stexlife.api.StexLifeVM;
import at.apf.stexlife.commons.Commons;
import at.apf.stexlife.exception.UncaughtExceptionException;
import at.apf.stexlife.parser.StexLifeCodeParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        StexLifeVM vm = new VMImpl(parser.parse(code), pluginRegistry);
        // TODO: Support program args
        try {
            vm.run("main");
        } catch (UncaughtExceptionException e) {
            System.err.println(e.getCause().getMessage());
            System.exit(1);
            return;
        }
    }
}
