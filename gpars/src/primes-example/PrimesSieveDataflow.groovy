import static groovyx.gpars.dataflow.Dataflow.task
import static groovyx.gpars.dataflow.Dataflow.operator
import groovyx.gpars.dataflow.stream.DataflowStream

/**
 * Demonstrates concurrent implementation of the Sieve of Eratosthenes using dataflow tasks and operators
 *
 * In principle, the algorithm consists of a concurrently run chained filters,
 * each of which detects whether the current number can be divided by a single prime number.
 * (generate nums 1, 2, 3, 4, 5, ...) -> (filter by mod 2) -> (filter by mod 3) -> (filter by mod 5) -> (filter by mod 7) -> (filter by mod 11) ->
 * The chain is built (grows) on the fly, whenever a new prime is found
 *
 * Won't run in the web console, use your local groovy console to enjoy
 * @author Vaclav Pech
 */

final int requestedPrimeNumberCount = 1000

final DataflowStream initialChannel = new DataflowStream()

/**
 * Generating candidate numbers
 */
task {
    (2..10000).each {
        initialChannel << it
    }
}

/**
 * Chain a new filter for a particular prime number to the end of the Sieve
 * @param inChannel The current end channel to consume
 * @param prime The prime number to divide future prime candidates with
 * @return A new channel ending the whole chain
 */
def filter(inChannel, int prime) {
    def outChannel = new DataflowStream()

    operator([inputs: [inChannel], outputs: [outChannel]]) {
        if (it % prime != 0) {
            bindOutput it
        }
    }
    return outChannel
}

/**
 * Consume Sieve output and add additional filters for all found primes
 */
def currentOutput = initialChannel
requestedPrimeNumberCount.times {
    int prime = currentOutput.val
    println "Found: $prime"
    currentOutput = filter(currentOutput, prime)
}