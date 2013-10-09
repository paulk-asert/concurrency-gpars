import groovy.transform.*

class ResourceProvider {
    private final Map<String, String> data = [:]

    @WithReadLock
    String getResource(String key) {
        data.get(key)
    }

    @WithWriteLock
    void refresh() {
        //reload the resources into memory
    }
}
