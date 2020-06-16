package at.apf.stexlife;

import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.StexLifeVM;
import at.apf.stexlife.api.exception.StexLifeException;
import at.apf.stexlife.api.plugin.StexLifeFunction;
import at.apf.stexlife.api.plugin.StexLifeModule;
import at.apf.stexlife.runtime.exception.NameNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginRegistryImpl implements PluginRegistry {

    private Map<String, Object> registry = new HashMap<>(); // <module, moduleObject>

    @Override
    public DataUnit call(StexLifeVM vm, String module, String function, DataUnit[] args) {
        Object mod = registry.get(module);
        Method fun = findMethod(module, function, args.length);

        try {
            if (fun.getParameterCount() > 0 && fun.getParameterTypes()[0].equals(StexLifeVM.class)) {
                // Function needs the VM instance
                Object[] args2 = new Object[args.length + 1];
                args2[0] = vm;
                for (int i = 0; i < args.length; i++) {
                    args2[i + 1] = args[i];
                }
                return (DataUnit) fun.invoke(mod, args2);
            } else {
                return (DataUnit) fun.invoke(mod, args);
            }
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
    public boolean isRegistered(String module, String function) {
        if (!registry.containsKey(module)) {
            return false;
        }
        return Stream.of(registry.get(module).getClass().getDeclaredMethods())
                .anyMatch(m -> m.isAnnotationPresent(StexLifeFunction.class)
                        && (m.getAnnotation(StexLifeFunction.class).value().equals(function) ||
                        (m.getAnnotation(StexLifeFunction.class).value().isEmpty() &&  m.getName().equals(function))));
    }

    @Override
    public List<String> getRegistrations(String module) {
        if (!registry.containsKey(module)) {
            throw new NameNotFoundException(module);
        }

        return Stream.of(registry.get(module).getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(StexLifeFunction.class))
                .map(m -> m.getAnnotation(StexLifeFunction.class).value().isEmpty() ?
                        m.getName() : m.getAnnotation(StexLifeFunction.class)
                .value()).collect(Collectors.toList());
    }

    private Method findMethod(String module, String function, int paramLength) {
        if (!registry.containsKey(module)) {
            return null;
        }
        Object mod = registry.get(module);
        Optional<Method> result = Stream.of(mod.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(StexLifeFunction.class)
                        && (m.getAnnotation(StexLifeFunction.class).value().equals(function) ||
                            (m.getAnnotation(StexLifeFunction.class).value().isEmpty() &&  m.getName().equals(function)))
                        && Stream.of(m.getParameterTypes()).filter(p -> p.equals(DataUnit.class)).count() == paramLength).findAny();

        return result.isPresent() ? result.get() : null;
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
                if (!m.getParameterTypes()[0].equals(StexLifeVM.class)) {
                    throw new RuntimeException("Only the first parameter can be a StexLifeVM, otherwise only DataUnit parameter types are allowed");
                }
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
