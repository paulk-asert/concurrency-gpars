package dataflow

import groovy.transform.Immutable
import groovyx.gpars.dataflow.DataflowQueue
import static groovyx.gpars.dataflow.Dataflow.task
import static groovyx.gpars.dataflow.Dataflow.select
import static groovyx.gpars.dataflow.Dataflow.DATA_FLOW_GROUP
import static java.lang.Math.random

@Immutable class Customer { Integer id }

def runSimulation(int numberOfCustomers, int numberOfWaitingSeats,
                  Closure hairTrimTime, Closure nextCustomerWaitTime) {
  def worldToShop = new DataflowQueue()
  def shopToBarber = new DataflowQueue()
  def barberToShop = new DataflowQueue()
  final barber = task {
    while (true) {
      def customer = shopToBarber.val
      if (customer == '') { break }
      assert customer instanceof Customer
      println "Barber : Starting Customer $customer.id"
      sleep hairTrimTime()
      println "Barber : Finished Customer $customer.id"
      barberToShop << customer
    }
  }
  final shop = task {
    def seatsTaken = 0
    def customersTurnedAway = 0
    def customersTrimmed = 0
    def isOpen = true
    mainloop:
    while (true) {
      def selector = select(barberToShop, worldToShop)
      def item = selector.select()
      switch (item.index) {
        case 0: //////// From the Barber ////////
          assert item.value instanceof Customer
          --seatsTaken
          ++customersTrimmed
          println "Shop : Customer $item.value.id leaving trimmed."
          if (!isOpen && (seatsTaken == 0)) {
            println "\nTrimmed $customersTrimmed and turned away $customersTurnedAway today."
            shopToBarber << ''
            break mainloop
          }
          break
        case 1: //////// From the World ////////
          if (item.value == '') { isOpen = false }
          else {
            assert item.value instanceof Customer
            if (seatsTaken < numberOfWaitingSeats) {
              ++seatsTaken
              println "Shop : Customer $item.value.id takes a seat. $seatsTaken in use."
              shopToBarber << item.value
            }
            else {
              println "Shop : Customer $item.value.id turned away."
              ++customersTurnedAway
            }
          }
          break
        default:
          throw new RuntimeException('Shop : Selected an non-existent queue.')
      }
    }
  }

  for (number in 0..<numberOfCustomers) {
    sleep nextCustomerWaitTime()
    println "World : Customer $number enters the shop."
    worldToShop << new Customer(number)
  }
  worldToShop << ''
  // We have to wait for the threads handling the dataflow system to terminate so that we can close it all
  // down and hence allow the script to terminate.
  [barber, shop]*.join()
  DATA_FLOW_GROUP.shutdown()
}

runSimulation(20, 4, {(random() * 60 + 10) as int}, {(random() * 20 + 10) as int})
