import groovy.transform.Immutable
import java.util.concurrent.ArrayBlockingQueue
import static java.lang.Math.random

@Immutable class Customer { Integer id }

def runSimulation(int numberOfCustomers, int numberOfWaitingSeats,
                  Closure hairTrimTime, Closure nextCustomerWaitTime) {
    final waitingChairs = new ArrayBlockingQueue<Customer>(numberOfWaitingSeats)
    final customersTurnedAway = 0
    final customersTrimmed = 0
    final barberThread = new Thread() {
        private working = true
        void stopWork() { working = false }
        @Override void run() {
            while (working || waitingChairs.size() > 0) {
                def customer = waitingChairs.take()
                assert customer instanceof Customer
                println "Barber : Starting Customer $customer.id"
                sleep hairTrimTime()
                println "Barber : Finished Customer $customer.id"
                ++customersTrimmed
                println "Shop : Customer $customer.id leaving trimmed."
            }
        }
    }
    barberThread.start()
    for (int i = 0; i < numberOfCustomers; ++i) {
        sleep nextCustomerWaitTime()
        println "World : Customer enters shop."
        final customer = new Customer(i)
        if (waitingChairs.offer(customer)) {
            println "Shop : Customer $customer.id takes a seat. ${waitingChairs.size()} in use."
        }
        else {
            ++customersTurnedAway
            println "Shop : Customer $customer.id turned away."
        }
    }
    barberThread.stopWork()
    barberThread.join()
    println "\nTrimmed $customersTrimmed and turned away $customersTurnedAway today."
}

runSimulation(20, 4, { (random() * 60 + 10) as int }, { (random() * 20 + 10) as int })
