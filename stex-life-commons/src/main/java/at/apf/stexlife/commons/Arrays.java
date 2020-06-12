package at.apf.stexlife.commons;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.exception.StexLifeException;
import at.apf.stexlife.api.plugin.StexLifeFunction;
import at.apf.stexlife.api.plugin.StexLifeModule;

@StexLifeModule("arrays")
public class Arrays {

    @StexLifeFunction
    public DataUnit size(DataUnit arr) {
        DataType.expecting(arr, DataType.ARRAY);
        return new DataUnit(arr.getArray().size(), DataType.INT);
    }

    @StexLifeFunction
    public void clear(DataUnit arr) {
        DataType.expecting(arr, DataType.ARRAY);
        arr.getArray().clear();
    }

    @StexLifeFunction
    public DataUnit indexOf(DataUnit arr, DataUnit elem) {
        DataType.expecting(arr, DataType.ARRAY);
        return new DataUnit(arr.getArray().indexOf(elem), DataType.INT);
    }

    @StexLifeFunction
    public void sort(DataUnit arr, DataUnit cmpFunction) {
        DataType.expecting(arr, DataType.ARRAY);
        DataType.expecting(cmpFunction, DataType.FUNCTION);
        try {
            // TODO: Implement
        } catch (Exception e) {
            throw new StexLifeException(e.getMessage());
        }
    }

    @StexLifeFunction
    public void removeAt(DataUnit arr, DataUnit index) {
        DataType.expecting(arr, DataType.ARRAY);
        DataType.expecting(index, DataType.INT);
        try {
            arr.getArray().remove(index.getInt().intValue());
        } catch (Exception e) {
            throw new StexLifeException(e.getMessage());
        }
    }
}
