package at.apf.stexlife.plugin;

import at.apf.stexlife.api.StexLifeVM;
import at.apf.stexlife.api.exception.InvalidTypeException;
import at.apf.stexlife.api.plugin.StexLifeFunction;
import at.apf.stexlife.api.plugin.StexLifeModule;
import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@StexLifeModule("hugo")
public class HugoPlugin {

    @StexLifeFunction
    public void clear(DataUnit arr) {
        if (arr.getType() != DataType.ARRAY) {
            throw new InvalidTypeException(arr.getType(), DataType.ARRAY);
        }
        arr.getArray().clear();
    }

    @StexLifeFunction("flip")
    public DataUnit rev(DataUnit arr) {
        if (arr.getType() != DataType.ARRAY) {
            throw new InvalidTypeException(arr.getType(), DataType.ARRAY);
        }
        List<DataUnit> flipped = new ArrayList<>();
        arr.getArray().stream().forEach(e -> flipped.add(0, e));
        return new DataUnit(flipped, DataType.ARRAY);
    }

    @StexLifeFunction("sort")
    public DataUnit sort(StexLifeVM vm, DataUnit arr, DataUnit cmp) {
        DataType.expecting(arr, DataType.ARRAY);
        DataType.expecting(cmp, DataType.FUNCTION);
        List<DataUnit> copy = new ArrayList<>(arr.getArray());
        return new DataUnit(copy.stream()
                .sorted((a, b) -> vm.run(cmp.getFunction(), new DataUnit[]{a, b})
                        .getInt().intValue()).collect(Collectors.toList()), DataType.ARRAY);
    }
}
