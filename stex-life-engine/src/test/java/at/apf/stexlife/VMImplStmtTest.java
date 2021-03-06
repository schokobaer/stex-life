package at.apf.stexlife;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.exception.UncaughtExceptionException;
import at.apf.stexlife.plugin.HugoPlugin;
import at.apf.stexlife.runtime.DataFrame;
import at.apf.stexlife.runtime.exception.NameNotFoundException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

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
    public void whileLoop_shouldBreakAfterRound10() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  while (true) {" +
                        "    a = a + 1;" +
                        "    if (a == 10) {" +
                        "      break;" +
                        "    }" +
                        "  } " +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(10L, result.getInt().longValue());
    }

    @Test
    public void whileLoopWithContinue_shouldSKipAfterRound5() {
        String code =
                "main() {" +
                        "  let sum = 0;" +
                        "  let i = 0;" +
                        "  while (i < 10) {" +
                        "    i = i + 1;" +
                        "    if (i > 5) {" +
                        "      continue;" +
                        "    }" +
                        "    sum = sum + i;" +
                        "  } " +
                        "  return sum;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(15L, result.getInt().longValue());
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

    @Test
    public void forLoopWithBreak_shouldRun5Times() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  for (let i = 0; i < 10; i = i + 1) {" +
                        "    a = a + 1;" +
                        "    if (a == 5) {" +
                        "      break;" +
                        "    }" +
                        "  } " +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(5L, result.getInt().longValue());
    }

    @Test
    public void forLoopWithContinue_shouldRun5Times() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  for (let i = 0; i < 10; i = i + 1) {" +
                        "    if (i == 5) {" +
                        "      continue;" +
                        "    }" +
                        "    a = a + 1;" +
                        "  } " +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(9L, result.getInt().longValue());
    }

    @Test
    public void loopVariableUsageAfterForLoop_shouldReturnUndefined() throws Throwable {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  for (let i = 0; i < 10; i = i + 1) {" +
                        "    a = a + 1;" +
                        "  } " +
                        "  return i;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(result.getType(), DataType.UNDEFINED);
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
    public void foreachLoopWithArray_shouldBreakAt2() {
        String code =
                "main() {" +
                        "  let a = [1, 2, 3];" +
                        "  let sum = 0;" +
                        "  foreach (let x in a) {" +
                        "    sum = sum + x;" +
                        "    if (x == 2) {" +
                        "      break;" +
                        "    }" +
                        "  } " +
                        "  return sum;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(3L, result.getInt().longValue());
    }

    @Test
    public void foreachLoopWithArray_shouldContinueAt2() {
        String code =
                "main() {" +
                        "  let a = [1, 2, 3];" +
                        "  let sum = 0;" +
                        "  foreach (let x in a) {" +
                        "    if (x == 2) {" +
                        "      continue;" +
                        "    }" +
                        "    sum = sum + x;" +
                        "  } " +
                        "  return sum;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(4L, result.getInt().longValue());
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

    @Test
    public void blockVariableAfterBlockCall_shouldReturnUndefined() throws Throwable {
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
        Assert.assertEquals(result.getType(), DataType.UNDEFINED);
    }

    @Test
    public void afterThrow_shouldCatchBlockGetExecuted() {
        String code =
                "main() {" +
                        "  try {" +
                        "    let a = 0;" +
                        "    throw {msg:\"foo\"};" +
                        "  } catch(e) {" +
                        "    return e.msg;" +
                        "  }" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.STRING, result.getType());
        Assert.assertEquals("foo", result.getString());
    }

    @Test
    public void afterThrowInFunction_shouldCatchBlockGetExecuted() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  try {" +
                        "    bar();" +
                        "  } catch(e) {" +
                        "    a = a + 1;" +
                        "  }" +
                        "  return a;" +
                        "}" +
                        "bar() { throw {msg:\"foo\"}; }";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1L, result.getInt().longValue());
    }

    @Test
    public void catchBlock_shouldNotGetExecuted() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  try {" +
                        "    a = 3;" +
                        "  } catch(e) {" +
                        "    a = 1;" +
                        "  }" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(3L, result.getInt().longValue());
    }

    @Test
    public void finallyBlock_shouldGetExecutedAfterTryWithoutReturn() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  try {" +
                        "    a = 3;" +
                        "  } catch(e) {" +
                        "    a = 1;" +
                        "  } finally {" +
                        "    a = a + 1;" +
                        "  }" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(4L, result.getInt().longValue());
    }

    @Test
    public void finallyBlock_shouldGetExecutedAfterTryWithReturn() {
        String code =
                "main() {" +
                        "  let obj = {a: 0};" +
                        "  try {" +
                        "    obj.a = 3;" +
                        "    return obj;" +
                        "  } catch(e) {" +
                        "    obj.a = 1;" +
                        "  } finally {" +
                        "    obj.a = obj.a + 1;" +
                        "  }" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.OBJECT, result.getType());
        Assert.assertEquals(4L, result.getObject().get("a").getInt().longValue());
    }

    @Test
    public void finallyBlock_shouldGetExecutedAfterCatchWithoutReturn() {
        String code =
                "main() {" +
                        "  let a = 0;" +
                        "  try {" +
                        "    a = 3;" +
                        "    throw {msg:\"foo\"};" +
                        "  } catch(e) {" +
                        "    a = 1;" +
                        "  } finally {" +
                        "    a = a + 1;" +
                        "  }" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(2L, result.getInt().longValue());
    }

    @Test
    public void finallyBlock_shouldGetExecutedAfterCatchWithReturn() {
        String code =
                "main() {" +
                        "  let obj = {a: 0};" +
                        "  try {" +
                        "    obj.a = 3;" +
                        "    throw {msg:\"foo\"};" +
                        "  } catch(e) {" +
                        "    obj.a = 1;" +
                        "    return obj;" +
                        "  } finally {" +
                        "    obj.a = obj.a + 1;" +
                        "  }" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.OBJECT, result.getType());
        Assert.assertEquals(2L, result.getObject().get("a").getInt().longValue());
    }

    @Test
    public void returnOfFinally_shouldReallyReturn() {
        String code =
                "main() {" +
                        "  let obj = {a: 0};" +
                        "  try {" +
                        "    obj.a = 3;" +
                        "  } catch(e) {" +
                        "    obj.a = 1;" +
                        "  } finally {" +
                        "    return 1;" +
                        "  }" +
                        "  return 0;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1L, result.getInt().longValue());
    }

    @Test
    public void pluginVoidFunctionCall_shouldClearArray() throws IOException {
        String code =
                "from hugo import clear;" +
                        "main() {" +
                        "  let arr = [1, 2, 3];" +
                        "  clear(arr);" +
                        "  return arr;" +
                        "}";
        PluginRegistryImpl pluginRegistry = new PluginRegistryImpl();
        pluginRegistry.register(new HugoPlugin());
        vm = new VMImpl(StexCodeParser.parse(code), pluginRegistry);
        vm.loadIncludes();
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.ARRAY, result.getType());
        Assert.assertEquals(0, result.getArray().size());
    }

    @Test
    public void pluginFunctionCallWithFunctionArgument_shouldRunInVM() throws IOException {
        String code =
                "from hugo import sort;" +
                        "main() {" +
                        "  let arr = [5, 3, 6, 2];" +
                        "  return sort(arr, (a,b){return a - b;});" +
                        "}";
        PluginRegistryImpl pluginRegistry = new PluginRegistryImpl();
        pluginRegistry.register(new HugoPlugin());
        vm = new VMImpl(StexCodeParser.parse(code), pluginRegistry);
        vm.loadIncludes();
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.ARRAY, result.getType());
        Assert.assertEquals(4, result.getArray().size());
        Assert.assertEquals(2, result.getArray().get(0).getInt().intValue());
        Assert.assertEquals(3, result.getArray().get(1).getInt().intValue());
        Assert.assertEquals(5, result.getArray().get(2).getInt().intValue());
        Assert.assertEquals(6, result.getArray().get(3).getInt().intValue());
    }

    @Test
    public void pluginFunctionCall_shouldReturnNewArray() throws IOException {
        String code =
                "from hugo import flip;" +
                        "main() {" +
                        "  let arr = [1, 2, 3];" +
                        "  return flip(arr);" +
                        "}";
        PluginRegistryImpl pluginRegistry = new PluginRegistryImpl();
        pluginRegistry.register(new HugoPlugin());
        vm = new VMImpl(StexCodeParser.parse(code), pluginRegistry);
        vm.loadIncludes();
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.ARRAY, result.getType());
        Assert.assertEquals(3, result.getArray().size());
        Assert.assertEquals(3, result.getArray().get(0).getInt().intValue());
        Assert.assertEquals(2, result.getArray().get(1).getInt().intValue());
        Assert.assertEquals(1, result.getArray().get(2).getInt().intValue());
    }

    @Test
    public void throwInPluginFunctionCall_shouldGetHandled() throws IOException {
        String code =
                "from hugo import flip;" +
                "main() {" +
                "  try {" +
                "    return flip(1);" +
                "  } catch(e) {" +
                "    return 0;" +
                "  }" +
                "}";
        PluginRegistryImpl pluginRegistry = new PluginRegistryImpl();
        pluginRegistry.register(new HugoPlugin());
        vm = new VMImpl(StexCodeParser.parse(code), pluginRegistry);
        vm.loadIncludes();
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(0, result.getInt().intValue());
    }

    @Test
    public void aliasedPluginVoidFunctionCall_shouldClearArray() throws IOException {
        String code =
                "from hugo import clear as trash;" +
                        "main() {" +
                        "  let arr = [1, 2, 3];" +
                        "  trash(arr);" +
                        "  return arr;" +
                        "}";
        PluginRegistryImpl pluginRegistry = new PluginRegistryImpl();
        pluginRegistry.register(new HugoPlugin());
        vm = new VMImpl(StexCodeParser.parse(code), pluginRegistry);
        vm.loadIncludes();
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.ARRAY, result.getType());
        Assert.assertEquals(0, result.getArray().size());
    }

    @Test
    public void ternaryExpression_shouldReturnFirstExpression() {
        String code =
                "main() {" +
                        "  return if 3 > 2 ? 1 : 0;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(1L, result.getInt().longValue());
    }

    @Test
    public void ternaryExpression_shouldReturnSecondExpression() {
        String code =
                "main() {" +
                        "  return if 1 > 2 ? 1 : 0;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(0L, result.getInt().longValue());
    }

    @Test
    public void shortAssignment_shouldIncrement() {
        String code =
                "main() {" +
                        "  let a = 3;" +
                        "  a += 1;" +
                        "  return a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(4L, result.getInt().longValue());
    }

    @Test
    public void shortAssignmentInObject_shouldIncrement() {
        String code =
                "main() {" +
                        "  let obj = {a:3};" +
                        "  obj.a += 1;" +
                        "  return obj.a;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(4L, result.getInt().longValue());
    }

    @Test
    public void shortAssignmentWithArray_shouldAddElement() {
        String code =
                "main() {" +
                        "  let arr = [1, 2];" +
                        "  arr[1] += 1;" +
                        "  return arr[1];" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.INT, result.getType());
        Assert.assertEquals(3, result.getInt().intValue());
    }

    @Test
    public void singleQuoteString_shouldGetParsedCorrectly() {
        String code =
                "main() {" +
                        "  let str = 'hallo';" +
                        "  return str;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.STRING, result.getType());
        Assert.assertEquals("hallo", result.getString());
    }

    @Test
    public void singleQuoteStringWithExpression_shouldEvaluateExpression() {
        String code =
                "main() {" +
                        "  let str = 'hallo${1 + 2}';" +
                        "  return str;" +
                        "}";
        vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.STRING, result.getType());
        Assert.assertEquals("hallo3", result.getString());
    }
}
