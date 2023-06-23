import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class Database {
    private Map<String, String> data;
    private final int maxNumOfReaders;
    private int currentNumOfReaders = 0;
    private final Set<Thread> readerThreads;
    private boolean isWriting = false;
    private Thread writerThread = null;
    private final ReentrantLock lock;
    private final Condition okToRead;
    private final Condition okToWrite;
    private int waitingWriters = 0;


    public Database(int maxNumOfReaders) {
        data = new HashMap<>();  // Note: You may add fields to the class and initialize them in here. Do not add parameters!
        this.maxNumOfReaders = maxNumOfReaders;
        readerThreads = new HashSet<>();
        lock = new ReentrantLock();
        okToRead = lock.newCondition();
        okToWrite = lock.newCondition();
    }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }

    /**
     * indicates is thread can read from data
     */
    public boolean readTryAcquire() {
        //return (!this.isWriting && currentNumOfReaders != maxNumOfReaders);
        //return (!this.isWriting && waitingWriters == 0 && currentNumOfReaders != maxNumOfReaders);
        lock.lock();
        try {
            if (isWriting || currentNumOfReaders == maxNumOfReaders) {
                return false;
            }
            currentNumOfReaders++;
            readerThreads.add(Thread.currentThread());
            return true;

        } finally {
            lock.unlock();
        }
    }

    /**
     * puts thread is wait mode if it's not allowed to read from data.
     */
    public void readAcquire(){
        lock.lock();
        try {
            while (isWriting || currentNumOfReaders == maxNumOfReaders || waitingWriters > 0)
                okToRead.await();

            currentNumOfReaders++;
            readerThreads.add(Thread.currentThread());

        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock();
        }

    }

    public void readRelease() throws IllegalMonitorStateException {
        lock.lock();
        try {
            if (!readerThreads.contains(Thread.currentThread())) {
                throw new IllegalMonitorStateException("Illegal read release attempt");
            }
            else {
                currentNumOfReaders--;
                readerThreads.remove(Thread.currentThread());
                //if (currentNumOfReaders == 0)
                    okToWrite.signalAll();
                //else if (currentNumOfReaders < maxNumOfReaders)
                    okToRead.signalAll();
            }
        }
        finally {
            lock.unlock();
        }
    }

    public void writeAcquire() {
        lock.lock();
        try {
            waitingWriters++;
            while (isWriting || currentNumOfReaders > 0)
                okToWrite.await();

            isWriting = true;
            writerThread = Thread.currentThread();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            waitingWriters--;
            lock.unlock();
        }
    }

    public boolean writeTryAcquire() {
        //return !isWriting && currentNumOfReaders == 0;
        lock.lock();
        try {
            if (isWriting || currentNumOfReaders > 0) {
                return false;
            }
            isWriting = true;
            writerThread = Thread.currentThread();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void writeRelease() throws IllegalMonitorStateException{
        lock.lock();
        try {
            if (writerThread == null || !writerThread.equals(Thread.currentThread())) {
                throw new IllegalMonitorStateException("Illegal write release attempt");
            }
            else {
                isWriting = false;
                writerThread = null;
                okToWrite.signalAll();
                okToRead.signalAll();
            }
        }
        finally {
            lock.unlock();
        }
    }
}