package at.apf.stexlife.commons;

import at.apf.stexlife.api.Converter;
import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.exception.StexLifeException;
import at.apf.stexlife.api.plugin.StexLifeFunction;
import at.apf.stexlife.api.plugin.StexLifeModule;

@StexLifeModule("types")
public class Types {

    @StexLifeFunction
    public DataUnit type(DataUnit x) {
        return new DataUnit(x.getType().name(), DataType.STRING);
    }

    @StexLifeFunction("int")
    public DataUnit integer() {
        return new DataUnit(DataType.INT.name(), DataType.STRING);
    }

    @StexLifeFunction("float")
    public DataUnit floatt() {
        return new DataUnit(DataType.FLOAT.name(), DataType.STRING);
    }

    @StexLifeFunction("bool")
    public DataUnit bool() {
        return new DataUnit(DataType.BOOL.name(), DataType.STRING);
    }

    @StexLifeFunction("null")
    public DataUnit nullt() {
        return new DataUnit(DataType.NULL.name(), DataType.STRING);
    }

    @StexLifeFunction("string")
    public DataUnit string() {
        return new DataUnit(DataType.STRING.name(), DataType.STRING);
    }

    @StexLifeFunction("array")
    public DataUnit array() {
        return new DataUnit(DataType.ARRAY.name(), DataType.STRING);
    }

    @StexLifeFunction("object")
    public DataUnit object() {
        return new DataUnit(DataType.OBJECT.name(), DataType.STRING);
    }

    @StexLifeFunction("function")
    public DataUnit function() {
        return new DataUnit(DataType.FUNCTION.name(), DataType.STRING);
    }

    @StexLifeFunction
    public DataUnit limited() {
        return new DataUnit(DataType.LIMITED.name(), DataType.STRING);
    }

    @StexLifeFunction("int")
    public DataUnit toInt(DataUnit x) {
        try {
            return new DataUnit(Long.parseLong(Converter.stringify(x)), DataType.INT);
        } catch (NumberFormatException e) {
            throw new StexLifeException(e.getMessage());
        }
    }

    @StexLifeFunction("float")
    public DataUnit toFloat(DataUnit x) {
        try {
            return new DataUnit(Double.parseDouble(Converter.stringify(x)), DataType.FLOAT);
        } catch (NumberFormatException e) {
            throw new StexLifeException(e.getMessage());
        }
    }

    @StexLifeFunction("bool")
    public DataUnit toBool(DataUnit x) {
        if (x.getType() == DataType.STRING) {
            return new DataUnit(x.getString().equals("true"), DataType.BOOL);
        }

        if (x.getType() == DataType.INT) {
            return new DataUnit(x.getInt().intValue() == 0, DataType.BOOL);
        }

        if (x.getType() == DataType.FLOAT) {
            return new DataUnit(x.getFloat().floatValue() == 0, DataType.BOOL);
        }

        throw new StexLifeException("Can not parse to bool: " + Converter.stringify(x));
    }

    @StexLifeFunction("string")
    public DataUnit toString(DataUnit x) {
        return new DataUnit(Converter.stringify(x), DataType.STRING);
    }

    @StexLifeFunction
    public DataUnit isNumber(DataUnit x) {
        return new DataUnit(x.getType() == DataType.INT || x.getType() == DataType.FLOAT, DataType.BOOL);
    }
}
