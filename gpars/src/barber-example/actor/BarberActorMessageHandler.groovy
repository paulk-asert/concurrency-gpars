package actor

import groovy.transform.Immutable

import static groovyx.gpars.actor.Actors.messageHandler
import static groovyx.gpars.actor.Actors.reactor
import static java.lang.Math.random

@Immutable class CustomerMH { Integer id }
@Immutable class SuccessfulCustomerMH { CustomerMH customer }

def runSimulation(int numberOfCustomers, int numberOfWaitingSeats,
                  Closure hairTrimTime, Closure nextCustomerWaitTime) {
  def barber = reactor { customer ->
    assert customer instanceof CustomerMH
    println "Barber : Starting Customer $customer.id"
    sleep hairTrimTime()
    println "Barber : Finished Customer $customer.id"
    new SuccessfulCustomerMH(customer)
  }
  def shop = messageHandler {
    def seatsTaken = 0
    def isOpen = true
    def customersTurnedAway = 0
    def customersTrimmed = 0
    when { CustomerMH message ->
      if (seatsTaken <= numberOfWaitingSeats) {
        ++seatsTaken
        println "Shop : Customer $message.id takes a seat. $seatsTaken in use."
        barber << message
      } else {
        println "Shop : Customer $message.id turned away."
        ++customersTurnedAway
      }
    }
    when { SuccessfulCustomerMH message ->
      --seatsTaken
      ++customersTrimmed
      println "Shop : Customer $message.customer.id leaving trimmed."
      if (!isOpen && (seatsTaken == 0)) {
        println "\nTrimmed $customersTrimmed and turned away $customersTurnedAway today."
        stop()
      }
    }
    when { String message -> isOpen = false }
  }

  for (number in 0..<numberOfCustomers) {
    sleep nextCustomerWaitTime()
    println "World : Customer $number enters the shop."
    shop << new CustomerMH(number)
  }
  shop << ''
  shop.join()
}

runSimulation(20, 4, { (random() * 60 + 10) as int }, { (random() * 20 + 10) as int })
