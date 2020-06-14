package at.apf.stexlife.api;

import at.apf.stexlife.api.DataUnit;

public interface StexLifeVM {

    DataUnit run(String function);

    DataUnit run(String function, DataUnit[] args);

    DataUnit run(FunctionWrapper function, DataUnit[] args);

    void loadIncludes();

    /**
     * TODOS:
     *  - Add quickAssignments (x++, x--, x+=, ++x)
     *  - Introduce LIMITED type
     *  - import a whole module (access with module.function)
     *  - other source files and export
     *  - concatExpression support (zB person().name)
     *  - EL (expression language ${}} in strings
     */
}
