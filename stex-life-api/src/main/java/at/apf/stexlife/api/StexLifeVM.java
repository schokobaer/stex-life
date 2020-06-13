package at.apf.stexlife.api;

import at.apf.stexlife.api.DataUnit;

public interface StexLifeVM {

    DataUnit run(String function);

    DataUnit run(String function, DataUnit[] args);

    DataUnit run(FunctionWrapper function, DataUnit[] args);

    void loadIncludes();

    /**
     * TODOS:
     *  - Put Self Context into FunctionWrapper (operand Rule)
     *  - Add quickAssignments (x++, x--, x+=, ++x)
     *  - Introduce LIMITED type
     *  - add a bundle with a single name ???
     *  - other source files and export
     *  - concatExpression support (zB person().name)
     */
}
