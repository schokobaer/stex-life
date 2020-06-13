package at.apf.stexlife.commons;

import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.StexLifeVM;
import at.apf.stexlife.api.plugin.StexLifeFunction;
import at.apf.stexlife.api.plugin.StexLifeModule;

@StexLifeModule("parallels")
public class Parallels {

    @StexLifeFunction
    public DataUnit async(StexLifeVM vm, DataUnit function) {
        return null;
    }

    @StexLifeFunction
    public DataUnit await(StexLifeVM vm, DataUnit thread) {
        return null;
    }
}
