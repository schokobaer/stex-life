package at.apf.stexlife.runtime;

import at.apf.stexlife.api.DataUnit;

public class StexFrame {

    private StexFrame parent;
    private DataFrame dataFrame;
    private DataUnit self;
    private DataUnit result;

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

    public void enterDataFrame() {
        dataFrame = new DataFrame(dataFrame);
    }

    public void leafeDataFrame() {
        if (dataFrame.getParent() == null) {
            throw new RuntimeException("Top DataFrame can not be left");
        }
        dataFrame = dataFrame.getParent();
    }

    public void setSelf(DataUnit self) {
        this.self = self;
    }

    public DataUnit getSelf() {
        return self;
    }
}
