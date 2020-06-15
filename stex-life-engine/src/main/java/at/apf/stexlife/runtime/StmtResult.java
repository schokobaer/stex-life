package at.apf.stexlife.runtime;

public enum StmtResult {
    NONE,       // Go ahead
    RETURN,     // return from function
    BREAK,      // break loop
    CONTINUE;   // go to next iteration of loop
}
