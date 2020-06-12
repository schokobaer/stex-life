package at.apf.stexlife;

import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.StexLifeVM;

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
     * @param paramLength Length of the parameter list.
     * @return true if registered, otherwise false.
     */
    boolean isRegistered(String module, String function, int paramLength);

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
