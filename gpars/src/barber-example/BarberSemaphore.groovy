import java.util.concurrent.Semaphore
import static java.lang.Math.random

def runSimulation(int numCustomers, int numWaitingSeats,
                  Closure hairTrimTime, Closure nextCustomerWaitTime) {
    final customerSemaphore = new Semaphore(1)
    final barberSemaphore = new Semaphore(1)
    final accessSeatsSemaphore = new Semaphore(1)
    def turnedAway = 0
    def trimmed = 0
    def numFreeSeats = numWaitingSeats

    final barberThread = new Thread() {
        private working = true
        void stopWork() { working = false }
        @Override
        void run() {
            while (working) {
                customerSemaphore.acquire()
                accessSeatsSemaphore.acquire()
                ++numFreeSeats
                barberSemaphore.release()
                accessSeatsSemaphore.release()
                println "Barber: Starting Customer."
                sleep hairTrimTime()
                println "Barber: Finished Customer."
            }
        }
    }
    barberThread.start()

    (0..<numCustomers).collect { number ->
        println "World: Customer $number enters shop."
        def t = Thread.start {
            accessSeatsSemaphore.acquire()
            if (numFreeSeats > 0) {
                println "Shop: Customer $number takes a seat. ${numWaitingSeats - numFreeSeats} in use."
                --numFreeSeats
                customerSemaphore.release()
                accessSeatsSemaphore.release()
                barberSemaphore.acquire()
                println "Shop: Customer $number leaving trimmed."
                ++trimmed
            } else {
                accessSeatsSemaphore.release()
                println "Shop: Customer $number turned away."
                ++turnedAway
            }
        }
        sleep nextCustomerWaitTime()
        t
    }*.join()

    barberThread.stopWork()
    barberThread.join()
    println "\nTrimmed $trimmed and turned away $turnedAway today."
}

runSimulation(20, 4, { (random() * 60 + 10) as int }, { (random() * 20 + 10) as int })
