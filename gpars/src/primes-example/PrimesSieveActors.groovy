import groovyx.gpars.actor.DynamicDispatchActor

/**
 * Demonstrates concurrent implementation of the Sieve of Eratosthenes using actors
 *
 * In principle, the algorithm consists of a concurrently run chained filters,
 * each of which detects whether the current number can be divided by a single prime number.
 * (generate nums 1, 2, 3, 4, 5, ...) -> (filter by mod 2) -> (filter by mod 3) -> (filter by mod 5) -> (filter by mod 7) -> (filter by mod 11) ->
 * The chain is built (grows) on the fly, whenever a new prime is found
 *
 * Won't run in the web console, use the standalone groovy console instead.
 *
 * @author Vaclav Pech
 */

int requestedPrimeNumberBoundary = 1000

final def firstFilter = new FilterActor(2).start()

/**
 * Generating candidate numbers and sending them to the actor chain
 */
(2..requestedPrimeNumberBoundary).each {
    firstFilter it
}
firstFilter.sendAndWait 'Poisson'

/**
 * Filter out numbers that can be divided by a single prime number
 */
final class FilterActor extends DynamicDispatchActor {
    private final int myPrime
    private def follower

    def FilterActor(final myPrime) { this.myPrime = myPrime; }

/**
 * Try to divide the received number with the prime. If the number cannot be divided, send it along the chain.
 * If there's no-one to send it to, I'm the last in the chain, the number is a prime and so I will create and chain
 * a new actor responsible for filtering by this newly found prime number.
 */
    def onMessage(int value) {
        if (value % myPrime != 0) {
            if (follower) follower value
            else {
                println "Found $value"
                follower = new FilterActor(value).start()
            }
        }
    }

/**
 * Stop the actor on poisson reception
 */
    def onMessage(def poisson) {
        if (follower) {
            def sender = poisson.sender
            follower.sendAndContinue(poisson, {this.stop(); sender?.send('Done')})  //Pass the poisson along and stop after a reply
        } else {  //I am the last in the chain
            stop()
            reply 'Done'
        }
    }
}