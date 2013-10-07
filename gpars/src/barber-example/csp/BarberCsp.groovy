package csp

import groovyx.gpars.csp.PAR
import org.jcsp.lang.CSProcess
import org.jcsp.util.Buffer
import org.jcsp.lang.Channel

import groovy.transform.Immutable
import groovyx.gpars.csp.ALT
import static java.lang.Math.random

@Immutable class Customer { Integer id }

def runSimulation(int numCustomers, int numWaitingSeats,
                  Closure hairTrimTime, Closure nextCustomerWaitTime) {
  final worldToShopChannel = Channel.one2one()
  final shopToBarberChannel = Channel.one2one(new Buffer(numWaitingSeats))
  final barberToShopChannel = Channel.one2one()
  final CSProcess barber = {
    final fromShopChannel = shopToBarberChannel.in()
    final toShopChannel = barberToShopChannel.out()
    while (true) {
      final customer = fromShopChannel.read()
      if (customer == "") { break }
      assert customer instanceof Customer
      println "Barber : Starting Customer $customer.id"
      sleep hairTrimTime()
      println "Barber : Finished Customer $customer.id"
      toShopChannel.write customer
    }
  }

  final CSProcess shop = {
    final fromBarberChannel = barberToShopChannel.in()
    final fromWorldChannel = worldToShopChannel.in()
    final toBarberChannel = shopToBarberChannel.out()
    final selector = new ALT([fromBarberChannel, fromWorldChannel])
    def seatsTaken = 0
    def turnedAway = 0
    def trimmed = 0
    def isOpen = true
    mainloop:
    while (true) {
      switch (selector.select()) {
        case 0: // From the Barber
          def customer = fromBarberChannel.read()
          assert customer instanceof Customer
          --seatsTaken
          ++trimmed
          println "Shop : Customer $customer.id leaving trimmed."
          if (!isOpen && (seatsTaken == 0)) {
            println "\nTrimmed $trimmed and turned away $turnedAway today."
            toBarberChannel.write ""
            break mainloop
          }
          break
        case 1: // From the World
          def customer = fromWorldChannel.read()
          if (customer == "") { isOpen = false }
          else {
            assert customer instanceof Customer
            if (seatsTaken < numWaitingSeats) {
              ++seatsTaken
              println "Shop : Customer $customer.id takes a seat. $seatsTaken in use."
              toBarberChannel.write customer
            }
            else {
              println "Shop : Customer $customer.id turned away."
              ++turnedAway
            }
          }
          break
        default:
          throw new RuntimeException("Shop : Selected a non-existent channel.")
      }
    }
  }

  final CSProcess world = {
    def toShopChannel = worldToShopChannel.out()
    for (number in 0..<numCustomers) {
      sleep nextCustomerWaitTime()
      println "World : Customer $number enters the shop."
      toShopChannel.write new Customer(number)
    }
    toShopChannel.write ""
  }

  new PAR([barber, shop, world]).run()
}

runSimulation(20, 4, {(random() * 60 + 10) as int}, {(random() * 20 + 10) as int})