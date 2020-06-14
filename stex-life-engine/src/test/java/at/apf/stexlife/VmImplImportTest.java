package at.apf.stexlife;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.plugin.HugoPlugin;
import org.junit.Assert;
import org.junit.Test;

public class VmImplImportTest {

    private VMImpl vm;

    @Test
    public void importFullModule_shouldWork() {
        String code =
                "import hugo;" +
                        "main() {" +
                        "  let arr = [1, 2, 3, 4];" +
                        "  hugo.clear(arr);" +
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
    public void importFullModuleWithAlias_shouldWork() {
        String code =
                "import hugo as h;" +
                "main() {" +
                "  let arr = [1, 2, 3, 4];" +
                "  h.clear(arr);" +
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
}
