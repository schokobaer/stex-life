package at.apf.stexlife.runtime;

import at.apf.stexlife.data.DataUnit;
import at.apf.stexlife.runtime.exception.NameNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class DataFrame {

    private DataFrame parent;
    private Map<String, DataUnit> variables = new HashMap<>();

    public DataFrame(DataFrame parent) {
        this.parent = parent;
    }

    public DataFrame getParent() {
        return parent;
    }

    public boolean contains(String name) {
        if (variables.containsKey(name)) {
            return true;
        } else if (parent != null) {
            return parent.contains(name);
        }
        return false;
    }

    public void set(String name, DataUnit value) {
        variables.put(name, value);
    }

    public DataUnit get(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        } else if (parent != null) {
            return parent.get(name);
        } else {
            throw new NameNotFoundException(name);
        }
    }
}
