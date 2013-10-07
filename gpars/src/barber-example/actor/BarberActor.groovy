package actor

import groovy.transform.Immutable
import static groovyx.gpars.actor.Actors.actor
import static groovyx.gpars.actor.Actors.reactor
import static java.lang.Math.random

@Immutable class Customer { Integer id }
@Immutable class SuccessfulCustomer { Customer customer }

def runSimulation(int numCustomers, int numWaitingSeats,
                  Closure hairTrimTime, Closure nextCustomerWaitTime) {
    def barber = reactor { customer ->
        assert customer instanceof Customer
        println "Barber : Starting Customer $customer.id"
        sleep hairTrimTime()
        println "Barber : Finished Customer $customer.id"
        new SuccessfulCustomer(customer)
    }
    def shop = actor {
        def seatsTaken = 0
        def isOpen = true
        def turnedAway = 0
        def trimmed = 0
        loop {
            react { message ->
                switch (message) {
                    case Customer:
                        if (seatsTaken <= numWaitingSeats) {
                            ++seatsTaken
                            println "Shop : Customer $message.id takes a seat. $seatsTaken in use."
                            barber << message
                        } else {
                            println "Shop : Customer $message.id turned away."
                            ++turnedAway
                        }
                        break
                    case SuccessfulCustomer:
                        --seatsTaken
                        ++trimmed
                        println "Shop : Customer $message.customer.id leaving trimmed."
                        if (!isOpen && (seatsTaken == 0)) {
                            println "\nTrimmed $trimmed and turned away $turnedAway today."
                            stop()
                        }
                        break
                    case '': isOpen = false; break
                    default: throw new RuntimeException("Shop got a message of unexpected type ${message.class}")
                }
            }
        }
    }
    for (number in 0..<numCustomers) {
        sleep nextCustomerWaitTime()
        println "World : Customer $number enters the shop."
        shop << new Customer(number)
    }
    shop << ''
    shop.join()
}

runSimulation(20, 4, { (random() * 60 + 10) as int }, { (random() * 20 + 10) as int })
