package at.apf.stexlife;

import at.apf.stexlife.data.DataUnit;

public interface StexLifeVM {

    DataUnit run(String function, DataUnit[] params);

    /**
     * TODOS:
     *  - Test all arithmetic operations
     *  - Add if, while statement
     *  - Add functionCall statement
     *  - Add try/catch statement
     *  - Support for native functions (maybe modules)
     *  - Allow anonymous functions?
     */
}
