package at.apf.stexlife.runtime;

import at.apf.stexlife.data.DataUnit;

public class StaxFrame {

    private StaxFrame parent;
    private DataFrame dataFrame;
    private DataUnit result;
    private DataUnit exception;

    public StaxFrame(StaxFrame parent) {
        this.parent = parent;
        this.dataFrame = new DataFrame(null);
    }

    public StaxFrame(StaxFrame parent, DataFrame dataFrame) {
        this.parent = parent;
        this.dataFrame = dataFrame;
    }

    public StaxFrame getParent() {
        return parent;
    }

    public DataFrame getDataFrame() {
        return dataFrame;
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
