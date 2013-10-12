@Grab("io.vertx:vertx-core:2.0.2-final")
@GrabExclude("com.hazelcast:hazelcast")
import org.vertx.java.core.Handler
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean

import static org.vertx.java.core.VertxFactory.newVertx

def names = ['socrates ', 'plato    ', 'aristotle', 'descartes', 'nietzsche']

def vertx = newVertx()
def eb = vertx.eventBus()

class Fork implements Handler {
    String name
    private holder = []

    @Override
    void handle(Object message) {
        def msg = message.body()
        def index = msg[-1].toInteger()
        switch (msg) {
            case ~/Take\d/:
                if (!holder) {
                    holder << index
                    message.reply("Accepted$index")
                } else message.reply("Rejected$index")
                break
            case ~/Release\d/:
                assert holder == [index]
                holder = []
                break
            default: throw new IllegalStateException("Cannot process the message: $message")
        }
    }
}

def forks = (1..names.size()).collect {
    def fork = new Fork(name: "Fork$it")
    eb.registerHandler(fork.name, fork)
    fork
}

sleep 1000

class Philosopher extends Thread {
    private random = new Random()
    def alive = new AtomicBoolean(true)
    def processed
    String who
    int meals = 0
    def forks
    private responses = []
    private gotFork = { it =~ /Accepted\d/ }
    def eb

    void run() {
        assert forks.size() == 2
        think()
        while(alive) {
            processed = new CountDownLatch(1)
            (0..1).each{ num ->
                eb.send(forks[num].name, "Take$num", { reply ->
                    processResponse(reply.body())
                })
            }
            processed.await()
            think()
        }
    }

    void think() {
        println who + ' is thinking'
        sleep random.nextInt(2000)
    }

    void eat() {
        meals++
        println toString()
        sleep random.nextInt(2000)
    }

    String toString() {
        switch (meals) {
            case 0: return "$who has starved"
            case 1: return "$who has eaten once"
            default: return "$who has eaten $meals times"
        }
    }

    void processResponse(Object message) {
        responses << message
        if (responses.size() == 2) {
            if (responses.every(gotFork)) {
                eat()
            }
            responses.findAll(gotFork).each {
                int index = it[-1].toInteger()
                eb.send(forks[index].name, "Release$index")
            }
            responses = []
            processed.countDown()
        }
    }
}

def philosophers = (1..names.size()).collect {
    new Philosopher(who: names[it - 1], eb: eb, forks: [forks[it - 1], forks[it % names.size()]])
}

philosophers*.start()
sleep 20000
philosophers.each{ it.alive = false }
sleep 5000
println()
philosophers.each { println it }
