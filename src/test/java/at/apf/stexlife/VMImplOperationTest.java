package at.apf.stexlife;

import at.apf.stexlife.data.DataType;
import at.apf.stexlife.data.DataUnit;
import at.apf.stexlife.runtime.DataFrame;
import at.apf.stexlife.runtime.exception.NameNotFoundException;
import org.junit.Assert;
import org.junit.Test;

public class VMImplOperationTest {

    private VMImpl vm;

    @Test
    public void addTwoInt_shouldReturnNewInt() {
        String code =
                "main() {" +
                        "  let a = 1;" +
                        "  return a + 4;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(5, result.getInt().intValue());
    }

    @Test
    public void addFloatToInt_shouldReturnNewFloat() {
        String code =
                "main() {" +
                        "  let a = 1;" +
                        "  return a + 1.5;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.FLOAT, result.getType());
        Assert.assertEquals(2.5, result.getFloat().doubleValue(), 0.000001);
    }

    @Test
    public void subTwoInt_shouldReturnNewInt() {
        String code =
                "main() {" +
                        "  let a = 4;" +
                        "  return a - 1;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(3, result.getInt().intValue());
    }

    @Test
    public void mulTwoInt_shouldReturnNewInt() {
        String code =
                "main() {" +
                        "  let a = 4;" +
                        "  return a * 2;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(8, result.getInt().intValue());
    }

    @Test
    public void divTwoInt_shouldReturnNewInt() {
        String code =
                "main() {" +
                        "  let a = 6;" +
                        "  return a / 3;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(2, result.getInt().intValue());
    }

    @Test
    public void divTwoInt_shouldReturnNewFlooredInt() {
        String code =
                "main() {" +
                        "  let a = 7;" +
                        "  return a / 3;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(2, result.getInt().intValue());
    }

    @Test
    public void modTwoInt_shouldReturnNewInt() {
        String code =
                "main() {" +
                        "  let a = 7;" +
                        "  return a % 3;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1, result.getInt().intValue());
    }

    @Test
    public void grtTwoInt_shouldReturnTrue() {
        String code =
                "main() {" +
                        "  let a = 6;" +
                        "  return a > 5;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.BOOL, result.getType());
        Assert.assertTrue(result.getBool().booleanValue());
    }

    @Test
    public void smeTwoInt_shouldReturnTrue() {
        String code =
                "main() {" +
                        "  let a = 6;" +
                        "  return a <= 6;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.BOOL, result.getType());
        Assert.assertTrue(result.getBool().booleanValue());
    }

    @Test
    public void inArray_shouldReturnTrue() {
        String code =
                "main() {" +
                        "  let a = [1,2,3,4,5];" +
                        "  let b = 4;" +
                        "  return b in a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.BOOL, result.getType());
        Assert.assertTrue(result.getBool().booleanValue());
    }

    @Test
    public void andOnTwoBool_ShouldBeTrue() {
        String code =
                "main() {" +
                        "  return true && true;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.BOOL, result.getType());
        Assert.assertTrue(result.getBool().booleanValue());
    }

    @Test
    public void arrayAddElem_ShouldWork() {
        String code =
                "main() {" +
                        "  let arr = [1,2,3];" +
                        "  return arr + 4;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.ARRAY, result.getType());
        Assert.assertEquals(4, result.getArray().size());
        Assert.assertEquals(4, result.getArray().get(3).getInt().intValue());
    }

    @Test
    public void elemAddArray_ShouldWork() {
        String code =
                "main() {" +
                        "  let arr = [1,2,3];" +
                        "  return 0 + arr;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.ARRAY, result.getType());
        Assert.assertEquals(4, result.getArray().size());
        Assert.assertEquals(0, result.getArray().get(0).getInt().intValue());
    }

    @Test
    public void stringAppend() {
        String code =
                "main() {" +
                        "  let s = \"foo\";" +
                        "  return s + \"bar\";" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.STRING, result.getType());
        Assert.assertEquals("foobar", result.getString());
    }

    @Test
    public void stringPrepand() {
        String code =
                "main() {" +
                        "  let s = \"foo\";" +
                        "  return \"bar\" + s;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.STRING, result.getType());
        Assert.assertEquals("barfoo", result.getString());
    }

    @Test
    public void objectAddObject() {
        String code =
                "main() {" +
                        "  let obj = {a: 1};" +
                        "  return obj + {b: 2};" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.OBJECT, result.getType());
        Assert.assertTrue(result.getObject().containsKey("a"));
        Assert.assertTrue(result.getObject().containsKey("b"));
        Assert.assertEquals(1, result.getObject().get("a").getInt().intValue());
        Assert.assertEquals(2, result.getObject().get("b").getInt().intValue());
    }

    @Test
    public void arrayMulArray() {
        String code =
                "main() {" +
                        "  let arr = [1, 2, 3];" +
                        "  return arr * [4, 5];" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.ARRAY, result.getType());
        Assert.assertEquals(5, result.getArray().size());
        Assert.assertEquals(1, result.getArray().get(0).getInt().intValue());
        Assert.assertEquals(5, result.getArray().get(4).getInt().intValue());
    }

    @Test
    public void arrayDivElem() {
        String code =
                "main() {" +
                        "  let arr = [1, 2, 3];" +
                        "  return arr / 2;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.ARRAY, result.getType());
        Assert.assertEquals(2, result.getArray().size());
        Assert.assertEquals(1, result.getArray().get(0).getInt().intValue());
        Assert.assertEquals(3, result.getArray().get(1).getInt().intValue());
    }

    @Test
    public void objectDivString() {
        String code =
                "main() {" +
                        "  let obj = {a: 1, b: 2};" +
                        "  return obj / \"b\";" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.OBJECT, result.getType());
        Assert.assertTrue(result.getObject().containsKey("a"));
        Assert.assertEquals(1, result.getObject().get("a").getInt().intValue());
    }

    @Test
    public void stringInString() {
        String code =
                "main() {" +
                        "  let str = \"foobar\";" +
                        "  return \"oba\" in str;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.BOOL, result.getType());
        Assert.assertTrue(result.getBool().booleanValue());
    }

    @Test
    public void elemInArray() {
        String code =
                "main() {" +
                        "  let arr = [1, 2, 3];" +
                        "  return 2 in arr;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.BOOL, result.getType());
        Assert.assertTrue(result.getBool().booleanValue());
    }

    @Test
    public void callFunctionShouldWork() {
        String code =
                "main() {" +
                        "  return sum(1, 2);" +
                        "}" +
                        "sum(a, b) {" +
                        "  return a + b;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(3L, result.getInt().longValue());
    }

    @Test(expected = NameNotFoundException.class)
    public void unknownFunctionCall_shouldThrowNameException() {
        String code =
                "main() {" +
                        "  return sumr(1, 2);" +
                        "}" +
                        "sum(a, b) {" +
                        "  return a + b;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
    }

    @Test
    public void voidFunctionCall_shouldWork() {
        String code =
                "main() {" +
                        "  let o = {a: 0};" +
                        "  foo(o);" +
                        "  return o.a;" +
                        "}" +
                        "foo(obj) {" +
                        "  obj.a = 1;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1L, result.getInt().longValue());
    }
}
