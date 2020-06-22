package at.apf.stexlife.commons;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.StexLifeVM;
import at.apf.stexlife.api.exception.StexLifeException;
import at.apf.stexlife.api.plugin.StexLifeFunction;
import at.apf.stexlife.api.plugin.StexLifeModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public DataUnit sort(StexLifeVM vm, DataUnit arr, DataUnit cmpFunction) {
        DataType.expecting(arr, DataType.ARRAY);
        DataType.expecting(cmpFunction, DataType.FUNCTION);
        try {
            return new DataUnit(arr.getArray().stream()
                    .sorted((a,b) -> vm.run(cmpFunction.getFunction(), new DataUnit[]{a, b}).getInt().intValue())
                    .collect(Collectors.toList()), DataType.ARRAY);
        } catch (Exception e) {
            throw new StexLifeException(e.getMessage());
        }
    }

    @StexLifeFunction
    public DataUnit filter(StexLifeVM vm, DataUnit arr, DataUnit filterFunction) {
        DataType.expecting(arr, DataType.ARRAY);
        DataType.expecting(filterFunction, DataType.FUNCTION);
        try {
            return new DataUnit(arr.getArray().stream()
                    .filter(a -> vm.run(filterFunction.getFunction(), new DataUnit[]{a}).getBool())
                    .collect(Collectors.toList()), DataType.ARRAY);
        } catch (Exception e) {
            throw new StexLifeException(e.getMessage());
        }
    }

    @StexLifeFunction
    public DataUnit map(StexLifeVM vm, DataUnit arr, DataUnit mapFunction) {
        DataType.expecting(arr, DataType.ARRAY);
        DataType.expecting(mapFunction, DataType.FUNCTION);
        try {
            return new DataUnit(arr.getArray().stream()
                    .map(a -> vm.run(mapFunction.getFunction(), new DataUnit[]{a}))
                    .collect(Collectors.toList()), DataType.ARRAY);
        } catch (Exception e) {
            throw new StexLifeException(e.getMessage());
        }
    }

    @StexLifeFunction
    public DataUnit group(StexLifeVM vm, DataUnit arr, DataUnit mapFunction) {
        DataType.expecting(arr, DataType.ARRAY);
        DataType.expecting(mapFunction, DataType.FUNCTION);
        try {
            Map<DataUnit, Map<String, DataUnit>> result = new HashMap<>();
            for (DataUnit x: arr.getArray()) {
                DataUnit key = vm.run(mapFunction.getFunction(), new DataUnit[]{x});
                if (!result.containsKey(key)) {
                    result.put(key, new HashMap<>());
                    result.get(key).put("key", key);
                    result.get(key).put("value", new DataUnit(new ArrayList<DataUnit>(), DataType.ARRAY));
                }
                result.get(key).get("value").getArray().add(x);
            }
            return new DataUnit(result.values().stream()
                    .map(m -> new DataUnit(m, DataType.OBJECT))
                    .collect(Collectors.toList()), DataType.ARRAY);
        } catch (Exception e) {
            throw new StexLifeException(e.getMessage());
        }
    }

    @StexLifeFunction
    public DataUnit reduce(StexLifeVM vm, DataUnit arr, DataUnit reduceFunction) {
        DataType.expecting(arr, DataType.ARRAY);
        DataType.expecting(reduceFunction, DataType.FUNCTION);
        try {
            if (arr.getArray().size() == 0) {
                return null;
            }
            DataUnit result = arr.getArray().get(0);
            for (int i = 1; i < arr.getArray().size(); i++) {
                result = vm.run(reduceFunction.getFunction(), new DataUnit[]{result, arr.getArray().get(i)});
            }
            return result;
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
