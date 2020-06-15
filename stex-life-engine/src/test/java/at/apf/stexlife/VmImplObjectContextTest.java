package at.apf.stexlife;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.exception.UncaughtExceptionException;
import at.apf.stexlife.runtime.exception.NameNotFoundException;
import org.junit.Assert;
import org.junit.Test;

public class VmImplObjectContextTest {

    private VMImpl vm;

    @Test
    public void callAnonymousFunctionOfObject() {
        String code =
                "main() {" +
                        "  let p = person();" +
                        "  return p.hello();" +
                        "}" +
                        "person() {" +
                        "  return {" +
                        "    hello: () { return \"foo\"; }" +
                        "  };" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.STRING, result.getType());
        Assert.assertEquals("foo", result.getString());
    }

    @Test
    public void thisInAnonymousFunctionInObject_shouldGetResolvedCorrectly() {
        String code =
                "main() {" +
                        "  let p = person();" +
                        "  return p.hello();" +
                        "}" +
                        "person() {" +
                        "  return {" +
                        "    name: \"foo\"," +
                        "    hello: () { return this.name; }" +
                        "  };" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.STRING, result.getType());
        Assert.assertEquals("foo", result.getString());
    }

    @Test
    public void thisInAnonymousFunction_shouldGetResolvedCorrectlyInFunctionCall() {
        String code =
                "main() {" +
                        "  let p = person();" +
                        "  return p.hello();" +
                        "}" +
                        "person() {" +
                        "  return {" +
                        "    name: () { return \"foo\"; }," +
                        "    hello: () { return this.name(); }" +
                        "  };" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.STRING, result.getType());
        Assert.assertEquals("foo", result.getString());
    }

    @Test(expected = NameNotFoundException.class)
    public void thisInNonSelfFunctionRef_shouldThrowNameException() {
        String code =
                "main() {" +
                        "  let p = {id: 1, getId: gi};" +
                        "  return p.getId();" +
                        "}" +
                        "gi() {" +
                        "  return this.id;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        try {
            DataUnit result = vm.run("main");
        } catch (UncaughtExceptionException e) {
            throw (RuntimeException) e.getCause();
        }
    }

    @Test
    public void thisInSelfFunctionRef_shouldResolveCorrectly() {
        String code =
                "main() {" +
                        "  let p = {id: 1, getId: ::gi};" +
                        "  return p.getId();" +
                        "}" +
                        "gi() {" +
                        "  return this.id;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1, result.getInt().intValue());
    }

    @Test
    public void thisInAnonymousFunctionAssign_shouldResolveCorrectly() {
        String code =
                "main() {" +
                        "  let p = {id: 1, getId: null};" +
                        "  p.getId = () { return this.id; };" +
                        "  return p.getId();" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1, result.getInt().intValue());
    }

    @Test
    public void thisInSelfFunctionRefAssign_shouldResolveCorrectly() {
        String code =
                "main() {" +
                        "  let p = {id: 1, getId: null};" +
                        "  p.getId = ::gi;" +
                        "  return p.getId();" +
                        "}" +
                        "gi() {" +
                        "  return this.id;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1, result.getInt().intValue());
    }
}
