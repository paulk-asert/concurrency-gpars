class SynchronizedOldSchool {
    private final myLock = new Object()

    synchronized
    static void greet() {
        println "world"
    }

    synchronized
    int answerToEverything() {
        return 42
    }

    void foo() {
        synchronized(myLock) {
            println "bar"
        }
    }
}
