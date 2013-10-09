import groovy.transform.Synchronized

class SynchronizedAnnotation {
    private final myLock = new Object()

    @Synchronized
    static void greet() {
        println "world"
    }

    @Synchronized
    int answerToEverything() {
        return 42
    }

    @Synchronized("myLock")
    void foo() {
        println "bar"
    }
}
