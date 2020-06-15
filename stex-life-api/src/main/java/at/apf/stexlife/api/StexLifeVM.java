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
     *  - Add shortAssignments (a += 1, b *= 3)
     *  - Introduce LIMITED type
     *  - Introduce break and continue for loops
     *  - other source files and export
     *  - concatExpression support (zB person().name)
     *  - EL (expression language ${}} in strings
     */
}
