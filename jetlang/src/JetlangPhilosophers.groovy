@Grab('org.jetlang:jetlang:0.2.10')
import groovy.transform.Immutable
import org.jetlang.core.Callback
import org.jetlang.fibers.ThreadFiber
import org.jetlang.channels.Request
import org.jetlang.channels.AsyncRequest
import org.jetlang.channels.MemoryRequestChannel

def names = ['socrates', 'plato', 'aristotle', 'descartes', 'nietzsche']

class Philosopher implements Callback {
    private random = new Random()
    String name
    int meals = 0
    def forks
    private channels = [new MemoryRequestChannel(), new MemoryRequestChannel()]
    private req = new ThreadFiber()
    private reply = new ThreadFiber()
    private responses = []
    private gotFork = { it instanceof Accepted }

    void start() {
        assert forks.size() == 2
        req.start()
        reply.start()
        (0..1).each{ channels[it].subscribe(reply, forks[it]) }
        think()
    }

    void think() {
        println(name + ' is thinking')
        sleep random.nextInt(3000)
        (0..1).each{ AsyncRequest.withOneReply(req, channels[it], new Take(it), this); }
    }

    void eat() {
        meals++
        println toString()
        sleep random.nextInt(2000)
    }

    String toString() {
        switch (meals) {
            case 0: return "$name has starved"
            case 1: return "$name has eaten once"
            default: return "$name has eaten $meals times"
        }
    }

    void onMessage(Object message) {
        responses << message
        if (responses.size() == 2) {
            if (responses.every(gotFork)) {
                eat()
            }
            responses.findAll(gotFork).each {
                int index = it.index
                channels[index].publish(req, new Release(index), forks[index])
            }
            responses = []
            think()
        }
    }
}

class Fork implements Callback {
    String name
    def holder = []

    void onMessage(message) {
        def msg = message instanceof Request ? message.request : message
        def index = msg.index
        switch (msg) {
            case Take:
                if (!holder) {
                    holder << index
                    message.reply(new Accepted(index))
                } else message.reply(new Rejected(index))
                break
            case Release:
                assert holder == [index]
                holder = []
                break
            default: throw new IllegalStateException("Cannot process the message: $message")
        }
    }
}

@Immutable class Take { int index }
@Immutable class Accepted { int index }
@Immutable class Rejected { int index }
@Immutable class Release { int index }

def forks = (1..names.size()).collect { new Fork(name: "Fork $it") }
def philosophers = (1..names.size()).collect {
    new Philosopher(name: names[it - 1], forks: [forks[it - 1], forks[it % names.size()]])
}

philosophers*.start()
sleep 10000
philosophers.each { println it }
