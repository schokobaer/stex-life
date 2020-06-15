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

    @StexLifeFunction("class")
    public DataUnit getClass(DataUnit obj) {
        if (obj.getType() == DataType.OBJECT && obj.getObject().containsKey("_class")) {
            DataUnit _class = obj.getObject().get("_class");
            DataType.expecting(_class, DataType.LIMITED);
            if (_class.getContent() instanceof TypeTree) {
                return new DataUnit(((TypeTree)_class.getObject()).name, DataType.STRING);
            }
        }
        return new DataUnit(null, DataType.NULL);
    }

    @StexLifeFunction("class")
    public DataUnit createClass(DataUnit obj, DataUnit type) {
        DataType.expecting(obj, DataType.OBJECT);
        DataType.expecting(type, DataType.STRING);
        TypeTree parent = null;
        if (obj.getObject().containsKey("_class")) {
            parent = (TypeTree) obj.getObject().get("_class").getContent();
        }
        obj.getObject().put("_class", new DataUnit(new TypeTree(type.getString(), parent), DataType.LIMITED));
        return obj;
    }

    @StexLifeFunction
    public DataUnit typeOf(DataUnit obj, DataUnit type) {
        DataType.expecting(obj, DataType.OBJECT);
        DataType.expecting(obj, new DataType[]{ DataType.STRING, DataType.LIMITED });
        if (!obj.getObject().containsKey("_class")) {
            return new DataUnit(false, DataType.BOOL);
        }
        if (type.getType() == DataType.LIMITED) {
            TypeTree t = (TypeTree) obj.getObject().get("_class").getContent();
            while (t != null) {
                if (t == type.getContent()) {
                    return new DataUnit(true, DataType.BOOL);
                }
                t = t.parent;
            }
            return new DataUnit(false, DataType.BOOL);
        }
        TypeTree t = (TypeTree) obj.getObject().get("_class").getContent();
        while (t != null) {
            if (t.name.equals(type.getString())) {
                return new DataUnit(true, DataType.BOOL);
            }
            t = t.parent;
        }
        return new DataUnit(false, DataType.BOOL);
    }

    private class TypeTree {
        private String name;
        private TypeTree parent;

        public TypeTree(String name, TypeTree parent) {
            this.name = name;
            this.parent = parent;
        }

        public String getName() {
            return name;
        }

        public TypeTree getParent() {
            return parent;
        }
    }
}
