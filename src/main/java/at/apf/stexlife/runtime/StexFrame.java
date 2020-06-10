package at.apf.stexlife.runtime;

import at.apf.stexlife.data.DataUnit;

public class StexFrame {

    private StexFrame parent;
    private DataFrame dataFrame;
    private DataUnit result;
    private DataUnit exception;

    public StexFrame(StexFrame parent) {
        this.parent = parent;
        this.dataFrame = new DataFrame(null);
    }

    public StexFrame(StexFrame parent, DataFrame dataFrame) {
        this.parent = parent;
        this.dataFrame = dataFrame;
    }

    public StexFrame getParent() {
        return parent;
    }

    public DataFrame getDataFrame() {
        return dataFrame;
    }

    public void setResult(DataUnit result) {
        this.result = result;
    }

    public DataUnit getResult() {
        return result;
    }

    public void setException(DataUnit exception) {
        this.exception = exception;
    }

    public DataUnit getException() {
        return exception;
    }
}