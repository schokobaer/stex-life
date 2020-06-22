package at.apf.stexlife.commons;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.StexLifeVM;
import at.apf.stexlife.api.exception.InvalidTypeException;
import at.apf.stexlife.api.exception.StexLifeException;
import at.apf.stexlife.api.plugin.StexLifeFunction;
import at.apf.stexlife.api.plugin.StexLifeModule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@StexLifeModule("parallels")
public class Parallels {

    private ExecutorService executor;
    private AtomicInteger threadCount = new AtomicInteger(0);

    @StexLifeFunction
    public DataUnit async(StexLifeVM vm, DataUnit function, DataUnit args) {
        StexLifeVM vm2 = vm.copyForNewThread();
        DataType.expecting(function, DataType.FUNCTION);
        DataType.expecting(args, DataType.ARRAY);
        if (threadCount.incrementAndGet() == 1) {
            executor = Executors.newCachedThreadPool();
        }
        Future<DataUnit> future = executor.submit(() -> {
            try {
                return vm2.run(function.getFunction(), new DataUnit[]{args});
            } finally {
                if (threadCount.decrementAndGet() == 0) {
                    executor.shutdown();
                }
            }
        });
        return new DataUnit(future, DataType.LIMITED);
    }

    @StexLifeFunction
    public void sleep(DataUnit milis) {
        DataType.expecting(milis, DataType.INT);
        try {
            Thread.sleep(milis.getInt());
        } catch (InterruptedException e) {
            throw new StexLifeException(e);
        }
    }

    @StexLifeFunction
    public DataUnit finished(DataUnit parallel) {
        if (!(parallel.getContent() instanceof Future)) {
            throw new StexLifeException("No future");
        }
        Future<DataUnit> future = (Future<DataUnit>) parallel.getContent();
        return new DataUnit(future.isDone(), DataType.BOOL);
    }

    @StexLifeFunction
    public DataUnit interrupted() {
        return new DataUnit(Thread.currentThread().isInterrupted(), DataType.BOOL);
    }

    @StexLifeFunction
    public void interrupt(DataUnit parallel) {
        if (!(parallel.getContent() instanceof Future)) {
            throw new StexLifeException("No future");
        }
        ((Future<DataUnit>) parallel.getContent()).cancel(true);
    }

    @StexLifeFunction
    public DataUnit await(StexLifeVM vm, DataUnit parallel) {
        if (parallel.getType() == DataType.ARRAY) {
            List<DataUnit> result = new ArrayList<>();
            for (DataUnit p: parallel.getArray()) {
                result.add(awaitSingle(p));
            }
            return new DataUnit(result, DataType.ARRAY);
        } else {
            return awaitSingle(parallel);
        }
    }

    private DataUnit awaitSingle(DataUnit parallel) {
        if (!(parallel.getContent() instanceof Future)) {
            throw new StexLifeException("No future");
        }
        Future<DataUnit> future = (Future<DataUnit>) parallel.getContent();
        try {
            DataUnit result = future.get();
            if (result == null) {
                result = DataUnit.UNDEFINED;
            }
            return result;
        } catch (InterruptedException e) {
            throw new StexLifeException(e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof StexLifeException) {
                throw (StexLifeException) e.getCause();
            }
            throw new StexLifeException(e.getCause());
        }
    }
}
