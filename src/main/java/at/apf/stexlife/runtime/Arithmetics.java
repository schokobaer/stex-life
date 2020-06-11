package at.apf.stexlife.runtime;

import at.apf.stexlife.data.Converter;
import at.apf.stexlife.data.DataType;
import at.apf.stexlife.data.DataUnit;
import at.apf.stexlife.runtime.exception.InvalidTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Arithmetics {

    public static DataUnit Add(DataUnit left, DataUnit right) {
        if (left.getType() == DataType.ARRAY) {
            List<DataUnit> arr = new ArrayList<>(left.getArray());
            arr.add(right);
            return new DataUnit(arr, DataType.ARRAY);
        } else if (right.getType() == DataType.ARRAY) {
            List<DataUnit> arr = new ArrayList<>(right.getArray());
            arr.add(0, left);
            return new DataUnit(arr, DataType.ARRAY);
        } else if (left.getType() == DataType.STRING) {
            return new DataUnit(left.getString() + Converter.stringify(right), DataType.STRING);
        } else if (right.getType() == DataType.STRING) {
            return new DataUnit(Converter.stringify(left) + right.getString(), DataType.STRING);
        } else if (left.getType() == DataType.INT) {
            if (right.getType() == DataType.FLOAT) {
                return new DataUnit(left.getInt().doubleValue() + right.getFloat(), DataType.FLOAT);
            } else if (right.getType() == DataType.INT) {
                return new DataUnit(left.getInt() + right.getInt(), DataType.INT);
            }
        } else if (left.getType() == DataType.FLOAT) {
            if (right.getType() == DataType.FLOAT) {
                return new DataUnit(left.getFloat() + right.getFloat(), DataType.FLOAT);
            } else if (right.getType() == DataType.INT) {
                return new DataUnit(left.getFloat() + right.getInt().doubleValue(), DataType.FLOAT);
            }
        } else if (right.getType() == DataType.FLOAT) {
            if (left.getType() == DataType.FLOAT) {
                return new DataUnit(left.getFloat() + right.getFloat(), DataType.FLOAT);
            } else if (left.getType() == DataType.INT) {
                return new DataUnit(left.getInt().doubleValue() + right.getFloat(), DataType.FLOAT);
            }
        } else if (left.getType() == DataType.OBJECT && right.getType() == DataType.OBJECT) {
            Map<String, DataUnit> obj = new HashMap<>(left.getObject());
            right.getObject().entrySet().stream().forEach(prop -> obj.put(prop.getKey(), prop.getValue()));
            return new DataUnit(obj, DataType.OBJECT);
        }

        throw new InvalidTypeException(left.getType(), DataType.INT);
    }

    public static DataUnit Sub(DataUnit left, DataUnit right) {
        if (left.getType() == DataType.INT) {
            if (right.getType() == DataType.INT) {
                return new DataUnit(left.getInt() - right.getInt(), DataType.INT);
            } else if (right.getType() == DataType.FLOAT) {
                return new DataUnit(left.getInt().doubleValue() - right.getFloat(), DataType.FLOAT);
            }
        } else if (left.getType() == DataType.FLOAT) {
            if (left.getType() == DataType.INT) {
                return new DataUnit(left.getFloat() - right.getInt().doubleValue(), DataType.FLOAT);
            } else if (right.getType() == DataType.FLOAT) {
                return new DataUnit(left.getFloat() - right.getFloat(), DataType.FLOAT);
            }
        }

        throw new InvalidTypeException(left.getType(), DataType.INT);
    }

    public static DataUnit Mul(DataUnit left, DataUnit right) {
        if (left.getType() == DataType.INT) {
            if (right.getType() == DataType.INT) {
                return new DataUnit(left.getInt() * right.getInt(), DataType.INT);
            } else if (right.getType() == DataType.FLOAT) {
                return new DataUnit(left.getInt().doubleValue() * right.getFloat(), DataType.FLOAT);
            }
        } else if (left.getType() == DataType.FLOAT) {
            if (left.getType() == DataType.INT) {
                return new DataUnit(left.getFloat() * right.getInt().doubleValue(), DataType.FLOAT);
            } else if (right.getType() == DataType.FLOAT) {
                return new DataUnit(left.getFloat() * right.getFloat(), DataType.FLOAT);
            }
        } else if (left.getType() == DataType.ARRAY && right.getType() == DataType.ARRAY) {
            List<DataUnit> arr = new ArrayList<>(left.getArray());
            arr.addAll(right.getArray());
            return new DataUnit(arr, DataType.ARRAY);
        }

        throw new InvalidTypeException(left.getType(), DataType.INT);
    }

    public static DataUnit Div(DataUnit left, DataUnit right) {
        if (left.getType() == DataType.INT) {
            if (right.getType() == DataType.INT) {
                return new DataUnit(left.getInt() / right.getInt(), DataType.INT);
            } else if (right.getType() == DataType.FLOAT) {
                return new DataUnit(left.getInt().doubleValue() / right.getFloat(), DataType.FLOAT);
            }
        } else if (left.getType() == DataType.FLOAT) {
            if (left.getType() == DataType.INT) {
                return new DataUnit(left.getFloat() / right.getInt().doubleValue(), DataType.FLOAT);
            } else if (right.getType() == DataType.FLOAT) {
                return new DataUnit(left.getFloat() / right.getFloat(), DataType.FLOAT);
            }
        } else if (left.getType() == DataType.ARRAY) {
            List<DataUnit> arr = left.getArray().stream().filter(u -> !u.equals(right)).collect(Collectors.toList());
            return new DataUnit(arr, DataType.ARRAY);
        } else if (left.getType() == DataType.OBJECT && right.getType() == DataType.STRING) {
            Map<String, DataUnit> obj = new HashMap<>(left.getObject());
            if (obj.containsKey(right.getString())) {
                obj.remove(right.getString());
            }
            return new DataUnit(obj, DataType.OBJECT);
        }

        throw new InvalidTypeException(left.getType(), DataType.INT);
    }

    public static DataUnit Mod(DataUnit left, DataUnit right) {
        if (left.getType() == DataType.INT && right.getType() == DataType.INT) {
            return new DataUnit(left.getInt() % right.getInt(), DataType.INT);
        }

        throw new InvalidTypeException(left.getType(), DataType.INT);
    }

    public static DataUnit In(DataUnit left, DataUnit right) {
        if (right.getType() == DataType.STRING) {
            if (left.getType() != DataType.ARRAY && left.getType() != DataType.FUNCTION && left.getType() != DataType.OBJECT) {
                return new DataUnit(right.getString().contains(Converter.stringify(left)), DataType.BOOL);
            }
        } else if (right.getType() == DataType.ARRAY) {
            return new DataUnit(right.getArray().stream().anyMatch(e -> e.equals(left)), DataType.BOOL);
        }

        throw new InvalidTypeException(left.getType(), DataType.INT);
    }
}
