package at.apf.stexlife.commons;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.plugin.StexLifeFunction;
import at.apf.stexlife.api.plugin.StexLifeModule;

@StexLifeModule("maths")
public class Maths {

    @StexLifeFunction
    public DataUnit abs(DataUnit x) {
        DataType.expecting(x, DataType.INT, DataType.FLOAT);
        if (x.getType() == DataType.INT) {
            return new DataUnit(Math.abs(x.getInt().longValue()), DataType.INT);
        }
        return new DataUnit(Math.abs(x.getFloat().floatValue()), DataType.FLOAT);
    }

    @StexLifeFunction
    public DataUnit rnd() {
        return new DataUnit(Math.random(), DataType.FLOAT);
    }

    @StexLifeFunction
    public DataUnit sqrt(DataUnit x) {
        DataType.expecting(x, DataType.INT, DataType.FLOAT);
        if (x.getType() == DataType.INT) {
            return new DataUnit(Math.sqrt(x.getInt().longValue()), DataType.FLOAT);
        }
        return new DataUnit(Math.sqrt(x.getFloat().floatValue()), DataType.FLOAT);
    }

    @StexLifeFunction
    public DataUnit round(DataUnit x) {
        DataType.expecting(x, DataType.FLOAT);
        return new DataUnit(Math.round(x.getFloat().floatValue()), DataType.FLOAT);
    }

}
