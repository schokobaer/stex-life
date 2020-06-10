package at.apf.stexlife.data;

import java.util.HashMap;
import java.util.Map;

public class DataUnit implements Comparable<DataUnit> {

    private Object content;
    private DataType type;

    public DataUnit(Object content, DataType type) {
        this.content = content;
        this.type = type;
    }

    public DataType getType() {
        return type;
    }

    public Object getContent() {
        return content;
    }

    public Integer getInteger() {
        return (Integer) content;
    }

    public Double getFloat() {
        return (Double) content;
    }

    public Boolean getBool() {
        return (Boolean) content;
    }

    public String getString() {
        return (String) content;
    }

    public DataUnit[] getArray() {
        return (DataUnit[]) content;
    }

    @SuppressWarnings("unchecked")
    public Map<String, DataUnit> getObject() {
        return (Map<String, DataUnit>) content;
    }

    public DataUnit copy() {
        if(type == DataType.ARRAY) {
            DataUnit[] arr = new DataUnit[getArray().length];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = getArray()[i];
            }
            return new DataUnit(arr, type);
        }
        if(type == DataType.OBJECT) {
            Map<String, DataUnit> obj = new HashMap<>(getObject());
            return new DataUnit(obj, type);
        }
        return new DataUnit(content, type);
    }

    @Override
    public boolean equals(Object arg1) {
        if(arg1 == null || !arg1.getClass().equals(this.getClass()))
            return false;
        DataUnit that = (DataUnit)arg1;
        if(this.type != that.type)
            return false;
        if(this.type == DataType.INT) {
            return this.getInteger().equals(that.getInteger());
        }
        else if(this.type == DataType.FLOAT) {
            return this.getFloat().equals(that.getFloat());
        }
        else if(this.type == DataType.BOOL) {
            return this.getBool().equals(that.getBool());
        }
        else if(this.type == DataType.STRING) {
            return this.getString().equals(that.getString());
        }
        else if(this.type == DataType.NULL) {
            return true;
        }
        else if(this.type == DataType.ARRAY) {
            if(this.getArray().length != that.getArray().length)
                return false;
            for (int i = 0; i < getArray().length; i++) {
                if(!this.getArray()[i].equals(that.getArray()[i]))
                    return false;
            }
            return true;
        }
        else if(this.type == DataType.OBJECT) {
            if(this.getObject().size() != that.getObject().size())
                return false;
            for (Map.Entry<String, DataUnit> kp: this.getObject().entrySet()) {
                if(!kp.getValue().equals(that.getObject().get(kp.getKey())))
                    return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(DataUnit that) {

        if(type == DataType.NULL && that.getType() == DataType.NULL)
            return 0;
        if(type == DataType.NULL)
            return -1;
        if(that.getType() == DataType.NULL)
            return 1;

        if(type == DataType.ARRAY && that.type == DataType.ARRAY) {
            return getArray().length == that.getArray().length ? 0 :
                    getArray().length > that.getArray().length ? 1 : -1;
        }

        if(type == DataType.STRING && that.type == DataType.STRING) {
            return getString().compareTo(that.getString());
        }

        // Object/Pointer/Function => Exception
        // Object/Function/Pointer => NotAllowed
        if(getType() == DataType.OBJECT || getType() == DataType.OBJECT ||
                getType() == DataType.FUNCTION || getType() == DataType.FUNCTION ||
                getType() == DataType.ARRAY || getType() == DataType.ARRAY ||
                getType() == DataType.STRING || getType() == DataType.STRING) {
            throw new RuntimeException("Unvalid comparison" + getType().name() + " + " + getType().name());
        }

        //DataUnit d1 = Convert.toFloat(this);
        //DataUnit d2 = Convert.toFloat(that);

        //return d1.getFloat().compareTo(d2.getFloat());
        return -1;
    }

}
