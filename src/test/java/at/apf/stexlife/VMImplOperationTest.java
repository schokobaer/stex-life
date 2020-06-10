package at.apf.stexlife;

import at.apf.stexlife.data.DataType;
import at.apf.stexlife.data.DataUnit;
import at.apf.stexlife.runtime.DataFrame;
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
}
