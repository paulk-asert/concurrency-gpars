import java.util.concurrent.Semaphore
import static java.lang.Math.random

def runSimulation(int numberOfCustomers, int numberOfWaitingSeats,
                  Closure hairTrimTime, Closure nextCustomerWaitTime) {
    final customerSemaphore = new Semaphore(1)
    final barberSemaphore = new Semaphore(1)
    final accessSeatsSemaphore = new Semaphore(1)
    def customersTurnedAway = 0
    def customersTrimmed = 0
    def numberOfFreeSeats = numberOfWaitingSeats
    final barberThread = new Thread() {
        private working = true
        void stopWork() { working = false }
        @Override void run() {
            while (working) {
                customerSemaphore.acquire()
                accessSeatsSemaphore.acquire()
                ++numberOfFreeSeats
                barberSemaphore.release()
                accessSeatsSemaphore.release()
                println "Barber: Starting Customer."
                sleep hairTrimTime()
                println "Barber: Finished Customer."
            }
        }
    }
    barberThread.start()
    final customerThreads = []
    for (number in 0..<numberOfCustomers) {
        println "World: Customer enters shop."
        customerThreads << Thread.start {
            accessSeatsSemaphore.acquire()
            if (numberOfFreeSeats > 0) {
                println "Shop: Customer $number takes a seat. ${numberOfWaitingSeats - numberOfFreeSeats} in use."
                --numberOfFreeSeats
                customerSemaphore.release()
                accessSeatsSemaphore.release()
                barberSemaphore.acquire()
                println "Shop: Customer $number leaving trimmed."
                ++customersTrimmed
            }
            else {
                accessSeatsSemaphore.release()
                println "Shop: Customer $number turned away."
                ++customersTurnedAway
            }
        }
        sleep nextCustomerWaitTime()
    }
    customerThreads*.join()
    barberThread.stopWork()
    barberThread.join()
    println "\nTrimmed $customersTrimmed and turned away $customersTurnedAway today."
}

runSimulation(20, 4, { (random() * 60 + 10) as int }, { (random() * 20 + 10) as int })