package at.apf.stexlife;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import org.junit.Assert;
import org.junit.Test;

public class VmImplObjectContextTest {

    private VMImpl vm;

    @Test
    public void callFunctionOfObject() {
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
    public void thisShouldGetResolvedCorrectly() {
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
    public void thisShouldGetResolvedCorrectlyInFunctionCall() {
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

    @Test
    public void thisShouldGetResolvedCorrectlyInNamedFunctionCall() {
        String code =
                "main() {" +
                        "  let p = {id: 1, getId: gi};" +
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
