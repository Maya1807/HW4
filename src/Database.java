import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a thread-safe database with read and write access control.
 */
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

    /**
     * Constructs a new Database object with the specified maximum number of readers.
     *
     * @param maxNumOfReaders the maximum number of threads allowed to read concurrently
     */
    public Database(int maxNumOfReaders) {
        data = new HashMap<>();
        this.maxNumOfReaders = maxNumOfReaders;
        readerThreads = new HashSet<>();
        lock = new ReentrantLock();
        okToRead = lock.newCondition();
        okToWrite = lock.newCondition();
    }

    /**
     * Puts a key-value pair into the database.
     *
     * @param key   the key associated with the value
     * @param value the value to be stored
     */
    public void put(String key, String value) {
        data.put(key, value);
    }

    /**
     * Retrieves the value associated with the specified key from the database.
     *
     * @param key the key whose associated value is to be retrieved
     * @return the value associated with the key, or null if the key is not present in the database
     */
    public String get(String key) {
        return data.get(key);
    }

    /**
     * Attempts to acquire read access to the database.
     *
     * @return true if the current thread successfully acquires read access, false otherwise
     */
    public boolean readTryAcquire() {
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
     * Acquires read access to the database. If read access is not available, the thread will wait until it can acquire access.
     */
    public void readAcquire() {
        lock.lock();
        try {
            while (isWriting || currentNumOfReaders == maxNumOfReaders || waitingWriters > 0)
                okToRead.await();

            currentNumOfReaders++;
            readerThreads.add(Thread.currentThread());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Releases read access to the database.
     *
     * @throws IllegalMonitorStateException if the current thread does not hold read access
     */
    public void readRelease() throws IllegalMonitorStateException {
        lock.lock();
        try {
            if (!readerThreads.contains(Thread.currentThread())) {
                throw new IllegalMonitorStateException("Illegal read release attempt");
            } else {
                currentNumOfReaders--;
                readerThreads.remove(Thread.currentThread());
                okToWrite.signalAll();
                okToRead.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Acquires write access to the database. If write access is not available, the thread will wait until it can acquire access.
     */
    public void writeAcquire() {
        lock.lock();
        try {
            waitingWriters++;
            while (isWriting || currentNumOfReaders > 0)
                okToWrite.await();

            isWriting = true;
            writerThread = Thread.currentThread();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            waitingWriters--;
            lock.unlock();
        }
    }

    /**
     * Attempts to acquire write access to the database.
     *
     * @return true if the current thread successfully acquires write access, false otherwise
     */
    public boolean writeTryAcquire() {
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

    /**
     * Releases write access to the database.
     *
     * @throws IllegalMonitorStateException if the current thread does not hold write access
     */
    public void writeRelease() throws IllegalMonitorStateException {
        lock.lock();
        try {
            if (writerThread == null || !writerThread.equals(Thread.currentThread())) {
                throw new IllegalMonitorStateException("Illegal write release attempt");
            } else {
                isWriting = false;
                writerThread = null;
                okToWrite.signalAll();
                okToRead.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}
