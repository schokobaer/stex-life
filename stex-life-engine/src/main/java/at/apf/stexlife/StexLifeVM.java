package at.apf.stexlife;

import at.apf.stexlife.api.DataUnit;

public interface StexLifeVM {

    DataUnit run(String function, DataUnit[] params);

    /**
     * TODOS:
     *  - Add 'this' to object functions
     *  - Add quickAssignments (x++, x--, x+=, ++x)
     */
}
