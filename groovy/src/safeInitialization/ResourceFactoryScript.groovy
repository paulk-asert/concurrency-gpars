import net.jcip.annotations.ThreadSafe

@ThreadSafe
class ResourceFactory {
    private static class ResourceHolder {
        static Resource instance = new Resource()
    }

    static Resource getInstance() {
        ResourceHolder.instance
    }
}

println ResourceFactory.instance.dump()