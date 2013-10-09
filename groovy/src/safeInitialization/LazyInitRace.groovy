import net.jcip.annotations.NotThreadSafe

@NotThreadSafe
class LazyInitRace {
    private ExpensiveResource instance = null

    ExpensiveResource getInstance() {
        if (!instance) instance = new ExpensiveResource()
        instance
    }
}
