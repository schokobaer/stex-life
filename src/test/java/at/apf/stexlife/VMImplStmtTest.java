package at.apf.stexlife;

import at.apf.stexlife.data.DataType;
import at.apf.stexlife.data.DataUnit;
import at.apf.stexlife.runtime.DataFrame;
import at.apf.stexlife.runtime.exception.NameNotFoundException;
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

    @Test
    public void onlyElseifIsTrue() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  if (a > 0) {" +
                        "    a = 1;" +
                        "  } elseif (a == 0) {" +
                        "    a = 2;" +
                        "  } else {" +
                        "    a = 3;" +
                        "  }" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(2L, result.getInt().longValue());
    }

    @Test
    public void secondElseifIsTrue() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  if (a > 0) {" +
                        "    a = 1;" +
                        "  } elseif (a == 2) {" +
                        "    a = 2;" +
                        "  } elseif (a <= 0) {" +
                        "    a = 3;" +
                        "  } else {" +
                        "    a = 4;" +
                        "  }" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(3L, result.getInt().longValue());
    }

    @Test
    public void codeAfterReturnIsReached_shouldNotGetExecuted() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  if (a >= 0) {" +
                        "    return 1;" +
                        "  } " +
                        "  return 0;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1L, result.getInt().longValue());
    }

    @Test
    public void whileLoop_shouldRun10Times() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  while (a < 10) {" +
                        "    a = a + 1;" +
                        "  } " +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(10L, result.getInt().longValue());
    }

    @Test
    public void forLoop_shouldRun10Times() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  for (let i = 0; i < 10; i = i + 1) {" +
                        "    a = a + 1;" +
                        "  } " +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(10L, result.getInt().longValue());
    }

    @Test(expected = NameNotFoundException.class)
    public void loopVariableUsageAfterForLoop_shouldThrowNameException() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  for (let i = 0; i < 10; i = i + 1) {" +
                        "    a = a + 1;" +
                        "  } " +
                        "  return i;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        vm.run("main");
    }

    @Test
    public void foreachLoopWithArray_shouldReturnSum() {
        String code =
                "main() {" +
                        "  let a = [1, 2, 3];" +
                        "  let sum = 0;" +
                        "  foreach (let x in a) {" +
                        "    sum = sum + x;" +
                        "  } " +
                        "  return sum;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(6L, result.getInt().longValue());
    }

    @Test
    public void foreachLoopWithString_shouldReturnInvertedString() {
        String code =
                "main() {" +
                        "  let str = \"foobar\";" +
                        "  let inverted = \"\";" +
                        "  foreach (let s in str) {" +
                        "    inverted = s + inverted;" +
                        "  } " +
                        "  return inverted;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.STRING, result.getType());
        Assert.assertEquals("raboof", result.getString());
    }

    @Test
    public void foreachLoopWithObject_shouldConcatAllKeys() {
        String code =
                "main() {" +
                        "  let obj = {b: 1, a: 1, c: 1};" +
                        "  let concat = \"\";" +
                        "  foreach (let k in obj) {" +
                        "    concat = concat + k;" +
                        "  } " +
                        "  return concat;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.STRING, result.getType());
        Assert.assertEquals("abc", result.getString());
    }

    @Test
    public void blockShouldRunCorrectly() {
        String code =
                "main() {" +
                        "  let a = 1;" +
                        "  {" +
                        "    let b = 2;" +
                        "    a = a + b;" +
                        "  }" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(3L, result.getInt().longValue());
    }

    @Test(expected = NameNotFoundException.class)
    public void blockVariableAfterBlockCall_shouldThrowNameException() {
        String code =
                "main() {" +
                "  let a = 1;" +
                "  {" +
                "    let b = 2;" +
                "    a = a + b;" +
                "  }" +
                "  return b;" +
                "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
    }
}
