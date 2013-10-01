import org.gruple.SpaceService
import org.gruple.Space

class Philosopher {
    private random = new Random()
    String name
    Space space
    private timesEaten = 0
    int id, num
    boolean done = false

    void run() {
        while (true) {
            think()
            if (done) return
            space.take(fork: id)
            space.take(fork: (id + 1) % num)
            eat()
            space.put(fork: id)
            space.put(fork: (id + 1) % num)
        }
    }

    void think() {
        println "$name is thinking"
        sleep random.nextInt(500)
    }

    void eat() {
        println "$name is EATING"
        timesEaten++
        sleep random.nextInt(1000)
    }

    String toString() {
        switch (timesEaten) {
            case 0: return "$name has starved"
            case 1: return "$name has eaten once"
            default: return "$name has eaten $timesEaten times"
        }
    }
}

def names = ['socrates', 'plato', 'aristotle', 'descartes', 'nietzsche']
def diningSpace = SpaceService.getSpace('Dining')
def philosophers = (0..<names.size()).collect{
    new Philosopher(name: names[it], id: it, space: diningSpace, num: names.size())
}
(0..<names.size()).each{ diningSpace << [fork: it] }
sleep 500
def threads = (0..<names.size()).collect{ n -> Thread.start{ philosophers[n].run() } }
sleep 10000
philosophers*.done = true
sleep 2000
threads.join()
println()
philosophers.each{ println it }
