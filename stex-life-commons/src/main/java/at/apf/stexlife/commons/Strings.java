package at.apf.stexlife.commons;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.exception.StexLifeException;
import at.apf.stexlife.api.plugin.StexLifeFunction;
import at.apf.stexlife.api.plugin.StexLifeModule;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@StexLifeModule("strings")
public class Strings {

    @StexLifeFunction
    public DataUnit len(DataUnit s) {
        DataType.expecting(s, DataType.STRING);
        return new DataUnit(s.getString().length(), DataType.INT);
    }

    @StexLifeFunction
    public DataUnit substr(DataUnit s, DataUnit start) {
        DataType.expecting(s, DataType.STRING);
        DataType.expecting(start, DataType.INT);
        try {
            return new DataUnit(s.getString().substring(start.getInt().intValue()), DataType.STRING);
        } catch (Exception e) {
            throw new StexLifeException(e.getMessage());
        }
    }

    @StexLifeFunction
    public DataUnit substr(DataUnit s, DataUnit start, DataUnit len) {
        DataType.expecting(s, DataType.STRING);
        DataType.expecting(start, DataType.INT);
        DataType.expecting(len, DataType.INT);
        try {
            return new DataUnit(s.getString().substring(start.getInt().intValue(),
                    len.getInt().intValue() - start.getInt().intValue()), DataType.STRING);
        } catch (Exception e) {
            throw new StexLifeException(e.getMessage());
        }
    }

    @StexLifeFunction
    public DataUnit split(DataUnit s, DataUnit regex) {
        DataType.expecting(s, DataType.STRING);
        DataType.expecting(regex, DataType.STRING);
        return new DataUnit(Stream.of(s.getString().split(regex.getString()))
                .map(x -> new DataUnit(x, DataType.STRING))
                .collect(Collectors.toList()), DataType.ARRAY);
    }

    @StexLifeFunction
    public DataUnit indexOf(DataUnit s, DataUnit part) {
        DataType.expecting(s, DataType.STRING);
        DataType.expecting(part, DataType.STRING);
        return new DataUnit(s.getString().indexOf(part.getString()), DataType.INT);
    }

    @StexLifeFunction
    public DataUnit indexOf(DataUnit s, DataUnit old, DataUnit replacement) {
        DataType.expecting(s, DataType.STRING);
        DataType.expecting(old, DataType.STRING);
        DataType.expecting(replacement, DataType.STRING);
        return new DataUnit(s.getString().replace(old.getString(), replacement.getString()), DataType.STRING);
    }

    @StexLifeFunction
    public DataUnit toUpper(DataUnit s) {
        DataType.expecting(s, DataType.STRING);
        return new DataUnit(s.getString().toUpperCase(), DataType.STRING);
    }

    @StexLifeFunction
    public DataUnit toLower(DataUnit s) {
        DataType.expecting(s, DataType.STRING);
        return new DataUnit(s.getString().toLowerCase(), DataType.STRING);
    }

    @StexLifeFunction
    public DataUnit trim(DataUnit s) {
        DataType.expecting(s, DataType.STRING);
        return new DataUnit(s.getString().trim(), DataType.STRING);
    }

}
