import static java.lang.management.ManagementFactory.getThreadMXBean

class DeadlockDetector {
    def deadlockPeriod = 2000

    void start() {
        new Timer("DeadlockDetector", true)
                .schedule({ checkForDeadlocks() } as TimerTask, 10, deadlockPeriod)
    }

    private checkForDeadlocks() {
        def ids = findDeadlockedThreads()
        if (ids)
            println '** Deadlock detected: ' +
                    ids.collect { findThread(threadMXBean.getThreadInfo(it)) }
        else
            println '** Nothing found'
    }

    private findDeadlockedThreads() {
        if (threadMXBean.synchronizerUsageSupported)
            threadMXBean.findDeadlockedThreads() // JDK 1.6
        else
            threadMXBean.findMonitorDeadlockedThreads()
    }

    private findThread(info) {
        Thread.allStackTraces.keySet().find { it.id == info.threadId } ?: '<unknown>'
    }

}
