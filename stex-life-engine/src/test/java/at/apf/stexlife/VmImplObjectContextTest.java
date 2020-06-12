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
}
