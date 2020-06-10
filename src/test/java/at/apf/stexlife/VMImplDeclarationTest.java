package at.apf.stexlife;

import at.apf.stexlife.data.DataType;
import at.apf.stexlife.data.DataUnit;
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
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        vm.run("main");
        DataFrame df = vm.getStexFrame().getDataFrame();
        Assert.assertTrue(df.contains("a"));
        Assert.assertEquals(DataType.INT, df.get("a").getType());
        Assert.assertEquals(1, df.get("a").getInt().intValue());
    }

    @Test
    public void declareFloat_ShouldDeclareAFloat() {
        String code =
                "main() {" +
                        "  let a = 1.2;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        vm.run("main");
        DataFrame df = vm.getStexFrame().getDataFrame();
        Assert.assertTrue(df.contains("a"));
        Assert.assertEquals(DataType.FLOAT, df.get("a").getType());
        Assert.assertEquals(1.2, df.get("a").getFloat().floatValue(), 0.000001);
    }

    @Test
    public void declareBool_ShouldDeclareABool() {
        String code =
                "main() {" +
                        "  let a = true;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        vm.run("main");
        DataFrame df = vm.getStexFrame().getDataFrame();
        Assert.assertTrue(df.contains("a"));
        Assert.assertEquals(DataType.BOOL, df.get("a").getType());
        Assert.assertTrue(df.get("a").getBool());
    }

    @Test
    public void declareNull_ShouldDeclareANull() {
        String code =
                "main() {" +
                        "  let a = null;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        vm.run("main");
        DataFrame df = vm.getStexFrame().getDataFrame();
        Assert.assertTrue(df.contains("a"));
        Assert.assertEquals(DataType.NULL, df.get("a").getType());
        Assert.assertNull(df.get("a").getContent());
    }

    @Test
    public void declareString_ShouldDeclareAString() {
        String code =
                "main() {" +
                        "  let a = \"foo\";" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        vm.run("main");
        DataFrame df = vm.getStexFrame().getDataFrame();
        Assert.assertTrue(df.contains("a"));
        Assert.assertEquals(DataType.STRING, df.get("a").getType());
        Assert.assertEquals("foo", df.get("a").getString());
    }

    @Test
    public void declareArray_ShouldDeclareAnArray() {
        String code =
                "main() {" +
                        "  let a = [1, \"bla\", true, null];" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        vm.run("main");
        DataFrame df = vm.getStexFrame().getDataFrame();
        Assert.assertTrue(df.contains("a"));
        Assert.assertEquals(DataType.ARRAY, df.get("a").getType());
        Assert.assertEquals(4, df.get("a").getArray().size());
    }

    @Test
    public void declareObject_ShouldDeclareAnObject() {
        String code =
                "main() {" +
                        "  let a = {id: 1, name: \"foo\"};" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        vm.run("main");
        DataFrame df = vm.getStexFrame().getDataFrame();
        Assert.assertTrue(df.contains("a"));
        Assert.assertEquals(DataType.OBJECT, df.get("a").getType());
        Assert.assertEquals(2, df.get("a").getObject().size());
        Map<String, DataUnit> obj = df.get("a").getObject();
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
