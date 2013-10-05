import java.util.concurrent.locks.ReentrantLock
import static java.lang.System.currentTimeMillis

class Philosopher3 extends Thread {
    ReentrantLock leftFork, rightFork
    private meals = 0
    private random = new Random()
    private start = currentTimeMillis()

    void run() {
        while (currentTimeMillis() - start < 10000) {
            think()
            eat()
        }
        println this
    }

    void think() {
        println "$name is thinking..."
        sleep random.nextInt(500)
        println "$name is hungry..."
    }

//    void eat() {
//        leftFork.lock()
//        sleep 100
//        rightFork.lock()
//        println "$name eats..."
//        sleep random.nextInt(500)
//        println "$name belches"
//        meals++
//        leftFork.unlock()
//        rightFork.unlock()
//    }

    void eat() {
        def (fork1, fork2) = [leftFork, rightFork]
        while (true) {
            fork1.lock()
            println "$name has fork#${fork1.hashCode()} ..."
            if (fork2.tryLock()) break
            println "$name cannot pickup second fork#${fork2.hashCode()} ..."
            fork1.unlock()
            (fork1, fork2) = [fork2, fork1]
        }
        println "$name has the second fork#${fork2.hashCode()} ..."
        println "$name eats..."
        sleep random.nextInt(500)
        println "$name belches"
        meals++
        leftFork.unlock()
        rightFork.unlock()
    }

    String toString() {
        switch (meals) {
            case 0: return "$name has starved"
            case 1: return "$name has eaten once"
            default: return "$name has eaten $meals times"
        }
    }
}

def names = ['socrates', 'plato', 'aristotle', 'descartes', 'nietzsche']
def forks = names.collect { new ReentrantLock() }
def philosophers = (0..<names.size()).collect { i ->
    new Philosopher3(name: names[i], leftFork: forks[i],
            rightFork: forks[(i + 1) % names.size()])
}

philosophers*.start()
philosophers*.join()
