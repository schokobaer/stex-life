package at.apf.stexlife;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.runtime.DataFrame;
import org.junit.Assert;
import org.junit.Test;

public class FibonacciTest {

    @Test
    public void runFibonacci() {
        String code = "main() {\n" +
                "    let n = 10;\n" +
                "    let arr = [];" +
                "    for(let i = 0; i < n; i = i + 1) {\n" +
                "        arr = arr + fibonacci(i);\n" +
                "    }\n" +
                "    return arr;" +
                "}\n" +
                "\n" +
                "fibonacci(n) {\n" +
                "    if (n <= 0) {\n" +
                "        return 0;\n" +
                "    }\n" +
                "    if (n < 3) {\n" +
                "        return 1;\n" +
                "    }\n" +
                "    return fibonacci(n - 1) + fibonacci(n - 2);\n" +
                "}";
        VMImpl vm = new VMImpl(StexCodeParser.parse(code));
        DataUnit result = vm.run("main");
        Assert.assertEquals(DataType.ARRAY, result.getType());
    }
}
