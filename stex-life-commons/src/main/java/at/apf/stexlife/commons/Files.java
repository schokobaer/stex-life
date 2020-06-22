package at.apf.stexlife.commons;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.exception.StexLifeException;
import at.apf.stexlife.api.plugin.StexLifeFunction;
import at.apf.stexlife.api.plugin.StexLifeModule;

import java.io.File;
import java.io.IOException;

@StexLifeModule("files")
public class Files {

    @StexLifeFunction
    public DataUnit file(DataUnit path) {
        DataType.expecting(path, DataType.STRING);
        return new DataUnit(new File(path.getString()), DataType.LIMITED);
    }

    @StexLifeFunction
    public DataUnit exists(DataUnit file) {
        DataType.expecting(file, DataType.LIMITED);
        if (file.getContent().getClass() != File.class) {
            throw new StexLifeException("File expected");
        }
        return new DataUnit(((File)file.getContent()).exists(), DataType.BOOL);
    }

    @StexLifeFunction
    public DataUnit isDirectory(DataUnit file) {
        DataType.expecting(file, DataType.LIMITED);
        if (file.getContent().getClass() != File.class) {
            throw new StexLifeException("File expected");
        }
        return new DataUnit(((File)file.getContent()).isDirectory(), DataType.BOOL);
    }

    @StexLifeFunction
    public DataUnit create(DataUnit file) {
        DataType.expecting(file, DataType.LIMITED);
        if (file.getContent().getClass() != File.class) {
            throw new StexLifeException("File expected");
        }
        try {
            return new DataUnit(((File)file.getContent()).createNewFile(), DataType.BOOL);
        } catch (IOException e) {
            throw new StexLifeException(e.getMessage());
        }
    }

    @StexLifeFunction
    public DataUnit delete(DataUnit file) {
        DataType.expecting(file, DataType.LIMITED);
        if (file.getContent().getClass() != File.class) {
            throw new StexLifeException("File expected");
        }
        return new DataUnit(((File)file.getContent()).delete(), DataType.BOOL);
    }

}
