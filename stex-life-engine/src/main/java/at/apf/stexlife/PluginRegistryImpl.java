package at.apf.stexlife;

import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.exception.StexLifeException;
import at.apf.stexlife.api.plugin.StexLifeFunction;
import at.apf.stexlife.api.plugin.StexLifeModule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PluginRegistryImpl implements PluginRegistry {

    private Map<String, Object> registry = new HashMap<>();

    @Override
    public DataUnit call(String module, String function, DataUnit[] args) {
        Object mod = registry.get(module);
        Method fun = findMethod(module, function, args.length);

        try {
            return (DataUnit) fun.invoke(mod, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof StexLifeException) {
                throw (StexLifeException) e.getTargetException();
            }
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isRegistered(String module, String function, int paramLength) {
        return findMethod(module, function, paramLength) != null;
    }

    private Method findMethod(String module, String function, int paramLength) {
        if (!registry.containsKey(module)) {
            return null;
        }
        Object mod = registry.get(module);
        return Stream.of(mod.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(StexLifeFunction.class)
                        && (m.getAnnotation(StexLifeFunction.class).value().equals(function) ||
                            (m.getAnnotation(StexLifeFunction.class).value().isEmpty() &&  m.getName().equals(function)))
                        && m.getParameterCount() == paramLength).findAny().orElseGet(null);
    }

    @Override
    public void register(Object obj) {
        if (!obj.getClass().isAnnotationPresent(StexLifeModule.class)) {
            throw new RuntimeException("Only objects of classes annotated with StexLifeModule are allowed");
        }

        StexLifeModule ann = obj.getClass().getAnnotation(StexLifeModule.class);
        if (registry.containsKey(ann.value())) {
            throw new RuntimeException("Module with name " + ann.value() + " is already registered");
        }

        List<String> funs = new LinkedList<>();
        Stream.of(obj.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(StexLifeFunction.class))
                .forEach(m -> {
            if (!m.getReturnType().equals(void.class) && !m.getReturnType().equals(DataUnit.class)) {
                throw new RuntimeException("Return type must be either void or DataUnit");
            }
            if (Stream.of(m.getParameterTypes()).anyMatch(t -> !t.equals(DataUnit.class))) {
                throw new RuntimeException("Only DataUnit parameter types are allowed");
            }
            String name = m.getAnnotation(StexLifeFunction.class).value();
            name = name.isEmpty() ? m.getName() : name;
            if (funs.contains(name + "_" + m.getParameterCount())) {
                throw new RuntimeException("Functions with same name need a different parameter length");
            }
            funs.add(name + "_" + m.getParameterCount());
        });

        registry.put(ann.value(), obj);
    }

    @Override
    public void register(Object[] bundle) {
        for (Object o: bundle) {
            register(o);
        }
    }
}
