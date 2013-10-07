// adapted from multiverse Groovy example, repo: http://git.codehaus.org/gitweb.cgi?p=multiverse.git
// file: multiverse-groovy/src/test/groovy/org/multiverse/integration/org/multiverse/integration/examples/DiningPhilosphersTest.groovy
package stm

import org.multiverse.api.TxnExecutor
import org.multiverse.api.references.TxnBoolean
import org.multiverse.api.references.TxnInteger

import static groovyx.gpars.stm.GParsStm.atomic
import static groovyx.gpars.stm.GParsStm.atomicWithInt
import static groovyx.gpars.stm.GParsStm.createTxnExecutor
import static org.multiverse.api.StmUtils.newTxnBoolean
import static org.multiverse.api.StmUtils.newTxnInteger

def food = newTxnInteger(10)
def names = ['socrates ', 'plato    ', 'aristotle', 'descartes', 'nietzsche']
def forks = (1..5).collect { new Fork(id: it, free: newTxnBoolean(true)) }
def philosophers = (1..5).collect {
    new Philosopher(name: names[it-1], food: food,
            left: forks[it-1], right: forks[it%5])
}
def threads = philosophers.collect { new Thread(it) }
threads*.start()
threads*.join()
philosophers.each { println it }

class Philosopher implements Runnable {
    String name
    Fork left, right
    TxnInteger timesEaten = newTxnInteger()
    TxnInteger food
    TxnExecutor block = createTxnExecutor(readTrackingEnabled: true)

    void eat() {
        atomic(block) {
            left.free.await(true)
            right.free.await(true)
            if (food.get() > 0) {
                left.take()
                right.take()
                timesEaten.increment()
                sleep 10
                food.decrement()
            }
        }
    }

    void think() {
        atomic(block) {
            left.release()
            right.release()
        }
        sleep 10
    }

    void run() {
        4.times { eat(); think() }
    }

    String toString() {
        int eaten = atomicWithInt { timesEaten.get() }
        switch (eaten) {
            case 0: return "$name has starved"
            case 1: return "$name has eaten once"
            default: return "$name has eaten $eaten times"
        }
    }
}

class Fork {
    int id
    TxnBoolean free
    void take() { free.set(false) }
    void release() { free.set(true) }
}
