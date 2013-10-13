import static java.lang.System.currentTimeMillis

class Philosopher2 extends Thread {
    def leftFork, rightFork
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
        sleep random.nextInt(2000)
        println "$name is hungry..."
    }

    void eat() {
        synchronized (leftFork) {
            sleep 1200 // 0-2000 gives (low-100%) likelihood of deadlock
            synchronized (rightFork) {
                println "$name eats..."
                sleep random.nextInt(1000)
                meals++
            }
        }
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
def forks = names.collect { new Object[0] }
def philosophers = (0..<names.size()).collect { i ->
    new Philosopher2(name: names[i], leftFork: forks[i],
            rightFork: forks[(i + 1) % names.size()])
}
new DeadlockDetector().start()
philosophers*.start()
philosophers*.join()
