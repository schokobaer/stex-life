package at.apf.stexlife;

import at.apf.stexlife.data.DataUnit;

public interface PluginRegistry {

    /**
     * Searchs for the function by name and amount of args and runs it.
     * @param name Name of the function.
     * @param args Argument list for the function call.
     * @return The result of the function execution.
     */
    DataUnit call(String name, DataUnit[] args);

    /**
     * Checks if the given function name with the given parameter list length is registered.
     * @param name Name of the function.
     * @param paramLength Length of the parameter list.
     * @return true if registered, otherwise false.
     */
    boolean isRegistered(String name, int paramLength);
}
