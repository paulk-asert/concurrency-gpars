import net.jcip.annotations.ThreadSafe

@ThreadSafe
class SafeLazyInitialization {
    private static ExpensiveResource resource

    synchronized static ExpensiveResource getInstance() {
        if (!resource) resource = new ExpensiveResource ()
        resource
    }
}