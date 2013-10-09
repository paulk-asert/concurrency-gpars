import net.jcip.annotations.ThreadSafe

@ThreadSafe
class EagerInitialization {
    static final resource = new Resource()
}