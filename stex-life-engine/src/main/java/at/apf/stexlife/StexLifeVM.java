package at.apf.stexlife;

import at.apf.stexlife.api.DataUnit;

public interface StexLifeVM {

    DataUnit run(String function);

    DataUnit run(String function, DataUnit[] params);

    void loadIncludes();

    /**
     * TODOS:
     *  - Add 'this' to object functions
     *  - Add quickAssignments (x++, x--, x+=, ++x)
     *  - Call stex functions from Plugin Function
     *  - Introduce LIMITED type
     *  - add a bundle with a single name ???
     *  - other source files and export
     *  - concatExpression support (zB person().name)
     */
}
