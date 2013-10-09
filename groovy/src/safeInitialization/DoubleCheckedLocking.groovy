import net.jcip.annotations.NotThreadSafe

@NotThreadSafe
class DoubleCheckedLocking {
    private static ExpensiveResource instance = null

    static ExpensiveResource getInstance() {
        if (!instance) {
            synchronized (DoubleCheckedLocking) {
                if (!instance) instance = new ExpensiveResource()
            }
        }
        instance
    }
}
