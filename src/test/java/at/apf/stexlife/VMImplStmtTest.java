package at.apf.stexlife;

import at.apf.stexlife.data.DataType;
import at.apf.stexlife.data.DataUnit;
import at.apf.stexlife.runtime.DataFrame;
import org.junit.Assert;
import org.junit.Test;

public class VMImplStmtTest {

    private VMImpl vm;

    @Test
    public void returnStmt_shouldReturnOne() {
        String code =
                "main() {" +
                        "  return 1;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1, result.getInt().intValue());
    }

    @Test
    public void assignStmt_shouldOverrideTheOldValueWithOne() {
        String code =
                "main() {" +
                        "  let a = 5;" +
                        "  a = 1;" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1L, result.getInt().longValue());
    }

    @Test
    public void assignStmtOnArray_shouldOverrideTheOldValueWithOne() {
        String code =
                "main() {" +
                        "  let a = [5, 6, 9];" +
                        "  a[1] = 1;" +
                        "  return a[1];" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1L, result.getInt().longValue());
    }

    @Test
    public void ifStmtIsTrue() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  if (a == 0) {" +
                        "    a = 1;" +
                        "  } else {" +
                        "    a = 3;" +
                        "  }" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1L, result.getInt().longValue());
    }

    @Test
    public void ifStmtIsFalse() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  if (a > 0) {" +
                        "    a = 1;" +
                        "  }" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(0L, result.getInt().longValue());
    }

    @Test
    public void ifStmtIsFalseSoElseIsExecuted() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  if (a > 0) {" +
                        "    a = 1;" +
                        "  } else {" +
                        "    a = 3;" +
                        "  }" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(3L, result.getInt().longValue());
    }
}
