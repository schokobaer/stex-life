package at.apf.stexlife.api;

import at.apf.stexlife.api.DataUnit;

import java.io.IOException;

public interface StexLifeVM {

    DataUnit run(String function);

    DataUnit run(String function, DataUnit[] args);

    DataUnit run(FunctionWrapper function, DataUnit[] args);

    void loadIncludes() throws IOException;

    /**
     * TODOS:
     *  - Introduce UNDEFINED -> Functions always point to implementation, only one function with same name allowed
     *    * in operand just evaluate to the function implementation or if its a plugin, stay by the name
     *    * function wrapper hold the module, where the function is hosted
     *    * plugin function of course can have the same name with different amount of params (cause its in java)
     *    * No name not found exception -> undefined DataUnit
     *  - concatExpression support (zB person().name)
     *  - EL (expression language ${}} in strings
     */
}
