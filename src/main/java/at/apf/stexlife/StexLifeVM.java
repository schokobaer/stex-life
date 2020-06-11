package at.apf.stexlife;

import at.apf.stexlife.data.DataUnit;

public interface StexLifeVM {

    DataUnit run(String function, DataUnit[] params);

    /**
     * TODOS:
     *  - Add try/catch statement
     *  - Support for native functions (maybe modules)
     */
}
