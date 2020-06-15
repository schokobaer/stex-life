package at.apf.stexlife;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.runtime.DataFrame;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class VMImplDeclarationTest {

    private VMImpl vm;

    @Test
    public void declareInt_ShouldDeclareAnInt() {
        String code =
                "main() {" +
                        "  let a = 1;" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1, result.getInt().intValue());
    }

    @Test
    public void declareNegativeInt_ShouldDeclareANegativeInt() {
        String code =
                "main() {" +
                        "  let a = -1;" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(-1, result.getInt().intValue());
    }

    @Test
    public void declareFloat_ShouldDeclareAFloat() {
        String code =
                "main() {" +
                        "  let a = 1.2;" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.FLOAT, result.getType());
        Assert.assertEquals(1.2, result.getFloat().floatValue(), 0.000001);
    }

    @Test
    public void declareNegativeFloat_ShouldDeclareANegativeFloat() {
        String code =
                "main() {" +
                        "  let a = -1.2;" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.FLOAT, result.getType());
        Assert.assertEquals(-1.2, result.getFloat().floatValue(), 0.000001);
    }

    @Test
    public void declareBool_ShouldDeclareABool() {
        String code =
                "main() {" +
                        "  let a = true;" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.BOOL, result.getType());
        Assert.assertTrue(result.getBool());
    }

    @Test
    public void declareNull_ShouldDeclareANull() {
        String code =
                "main() {" +
                        "  let a = null;" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.NULL, result.getType());
        Assert.assertNull(result.getContent());
    }

    @Test
    public void declareString_ShouldDeclareAString() {
        String code =
                "main() {" +
                        "  let a = \"foo\";" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.STRING, result.getType());
        Assert.assertEquals("foo", result.getString());
    }

    @Test
    public void declareArray_ShouldDeclareAnArray() {
        String code =
                "main() {" +
                        "  let a = [1, \"bla\", true, null];" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.ARRAY, result.getType());
        Assert.assertEquals(4, result.getArray().size());
    }

    @Test
    public void declareObject_ShouldDeclareAnObject() {
        String code =
                "main() {" +
                        "  let a = {id: 1, name: \"foo\"};" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.OBJECT, result.getType());
        Assert.assertEquals(2, result.getObject().size());
        Map<String, DataUnit> obj = result.getObject();
        Assert.assertTrue(obj.containsKey("id"));
        Assert.assertTrue(obj.containsKey("name"));
        Assert.assertEquals(DataType.INT, obj.get("id").getType());
        Assert.assertEquals(DataType.STRING, obj.get("name").getType());
        Assert.assertEquals(1, obj.get("id").getInt().intValue());
        Assert.assertEquals("foo", obj.get("name").getString());
    }

    @Test
    public void arrayAccess_shouldReturnOne() {
        String code =
                "main() {" +
                "  let a = [5, \"bla\", 1, null];" +
                "  return a[2];" +
                "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1, result.getInt().intValue());
    }

}
