package at.apf.stexlife.commons;

import at.apf.stexlife.api.DataType;
import at.apf.stexlife.api.DataUnit;
import at.apf.stexlife.api.exception.StexLifeException;
import at.apf.stexlife.api.plugin.StexLifeFunction;
import at.apf.stexlife.api.plugin.StexLifeModule;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@StexLifeModule("mutex")
public class Mutex {

    @StexLifeFunction
    public DataUnit lock() {
        return new DataUnit(new ReentrantLock(true), DataType.LIMITED);
    }

    @StexLifeFunction
    public void lock(DataUnit lock) {
        if (!(lock.getContent() instanceof Lock)) {
            throw new StexLifeException("No Lock");
        }
        ((Lock)lock.getContent()).lock();
    }

    @StexLifeFunction
    public DataUnit tryLock(DataUnit lock) {
        if (!(lock.getContent() instanceof Lock)) {
            throw new StexLifeException("No Lock");
        }
        return new DataUnit(((Lock)lock.getContent()).tryLock(), DataType.BOOL);
    }

    @StexLifeFunction
    public void unlock(DataUnit lock) {
        if (!(lock.getContent() instanceof Lock)) {
            throw new StexLifeException("No Lock");
        }
        ((Lock)lock.getContent()).unlock();
    }

    @StexLifeFunction
    public DataUnit semaphore(DataUnit permits) {
        DataType.expecting(permits, DataType.INT);
        return new DataUnit(new Semaphore(permits.getInt().intValue()), DataType.LIMITED);
    }

    @StexLifeFunction
    public void aquire(DataUnit semaphore) {
        DataType.expecting(semaphore, DataType.LIMITED);
        try {
            ((Semaphore)semaphore.getContent()).acquire();
        } catch (InterruptedException e) {
            throw new StexLifeException(e);
        }
    }

    @StexLifeFunction
    public void tryAquire(DataUnit semaphore) {
        DataType.expecting(semaphore, DataType.LIMITED);
        ((Semaphore)semaphore.getContent()).tryAcquire();
    }

    @StexLifeFunction
    public void release(DataUnit semaphore) {
        DataType.expecting(semaphore, DataType.LIMITED);
        ((Semaphore)semaphore.getContent()).release();
    }

}
