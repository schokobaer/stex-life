package at.apf.stexlife.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public Long getInt() {
        return (Long) content;
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

    public List<DataUnit> getArray() {
        return (List<DataUnit>) content;
    }

    @SuppressWarnings("unchecked")
    public Map<String, DataUnit> getObject() {
        return (Map<String, DataUnit>) content;
    }

    public DataUnit copy() {
        if(type == DataType.ARRAY) {
            List<DataUnit> arr = new ArrayList<>();
            for (int i = 0; i < arr.size(); i++) {
                arr.add(i, getArray().get(i));
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
            return this.getInt().equals(that.getInt());
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
        else if(this.type == DataType.NULL && that.type == DataType.NULL) {
            return true;
        }
        else if(this.type == DataType.ARRAY) {
            if(this.getArray().size() != that.getArray().size())
                return false;
            for (int i = 0; i < getArray().size(); i++) {
                if(!this.getArray().get(i).equals(that.getArray().get(i)))
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

        // Null
        if(type == DataType.NULL && that.getType() == DataType.NULL)
            return 0;
        if(type == DataType.NULL)
            return -1;
        if(that.getType() == DataType.NULL)
            return 1;

        // Numbers
        if (type == DataType.INT && that.type == DataType.INT) {
            return getInt().compareTo(that.getInt());
        } else if (type == DataType.INT && that.type == DataType.FLOAT) {
            return getInt() > that.getFloat() ? 1 : getInt() < that.getFloat() ? -1 : 0;
        } else if (type == DataType.FLOAT && that.type == DataType.INT) {
            return getInt() < that.getFloat() ? 1 : getInt() > that.getFloat() ? -1 : 0;
        } else if (type == DataType.FLOAT && that.type == DataType.FLOAT) {
            return getFloat().compareTo(that.getFloat());
        }

        // Bool
        if (type == DataType.BOOL && that.type == DataType.BOOL) {
            int a = getBool().booleanValue() ? 1 : 0;
            int b = that.getBool().booleanValue() ? 1 : 0;
            return a - b;
        }

        // String
        if (type == DataType.STRING && that.type == DataType.STRING) {
            return getString().compareTo(that.getString());
        }

        // Array
        if (type == DataType.ARRAY && that.type == DataType.ARRAY) {
            return getArray().size() - that.getArray().size();
        }

        if (type != that.type) {
            return Converter.stringify(this).compareTo(Converter.stringify(that));
        }

        return 0;


        /*if(type == DataType.ARRAY && that.type == DataType.ARRAY) {
            return getArray().size() == that.getArray().size() ? 0 :
                    getArray().size() > that.getArray().size() ? 1 : -1;
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
        return -1;*/
    }


    @Override
    public String toString() {
        return "DataUnit{" +
                  content + ":" + type.name() +
                '}';
    }
}
