package at.apf.stexlife.runtime;

import at.apf.stexlife.data.Converter;
import at.apf.stexlife.data.DataType;
import at.apf.stexlife.data.DataUnit;
import at.apf.stexlife.runtime.exception.InvalidTypeException;

public class Arithmetics {

    public static DataUnit Add(DataUnit left, DataUnit right) {
        if (left.getType() == DataType.ARRAY) {
            left.getArray().add(right);
        } else if (right.getType() == DataType.ARRAY) {
            right.getArray().add(0, left);
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
