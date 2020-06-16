package at.apf.stexlife;

import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.StexLifeVM;

import java.util.List;

public interface PluginRegistry {

    /**
     * Searchs for the function by name and amount of args and runs it.
     * @param function Name of the function.
     * @param args Argument list for the function call.
     * @return The result of the function execution.
     */
    DataUnit call(StexLifeVM vm, String module, String function, DataUnit[] args);

    /**
     * Checks if the given function name with the given parameter list length is registered.
     * @param function Name of the function.
     * @return true if registered, otherwise false.
     */
    boolean isRegistered(String module, String function);

    /**
     * Returns all functions of a registered module.
     * @param module name of the module.
     * @return List of all functions of that module.
     */
    List<String> getRegistrations(String module);

    /**
     * Analyzes the {@link at.apf.stexlife.api.plugin.StexLifeModule} annotated object for
     * {@link at.apf.stexlife.api.plugin.StexLifeFunction} annotated methods, and adds them to the registry.
     * @param obj An {@link at.apf.stexlife.api.plugin.StexLifeModule} annotatedobject with
     *            {@link at.apf.stexlife.api.plugin.StexLifeFunction} annotated methods.
     */
    void register(Object obj);

    /**
     * Registers all gives objects.
     * @param bundle Bundle of plugin objects.
     */
    void register(Object[] bundle);
}
