import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.concurrent.locks.ReadWriteLock

class ResourceProviderTransformed {
    private final ReadWriteLock $reentrantlock = new ReentrantReadWriteLock()
    private final Map<String, String> data = [:]

    String getResource(String key) {
        $reentrantlock.readLock().lock()
        try {
            return data.get(key)
        } finally {
            $reentrantlock.readLock().unlock()
        }
    }

    void refresh() {
        $reentrantlock.writeLock().lock()
        try {
            //reload the resources into memory
        } finally {
            $reentrantlock.writeLock().unlock()
        }
    }
}
