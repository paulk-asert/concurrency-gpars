class SynchronizedTransformed {
    private static final $LOCK = new Object[0]
    private final $lock = new Object[0]
    private final myLock = new Object()

    static void greet() {
        synchronized ($LOCK) {
            println "world"
        }
    }

    int answerToEverything() {
        synchronized ($lock) {
            return 42
        }
    }

    void foo() {
        synchronized (myLock) {
            println "bar"
        }
    }
}
 