@Grab('com.typesafe.akka:akka-actor_2.10:2.2.1')
import akka.actor.*
import groovy.transform.Immutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration

import static akka.pattern.Patterns.ask

@Immutable class Philosopher extends UntypedActor {
    private Random random = new Random()
    String name
    List forks = []

    static Props mkProps(String name, ActorRef left, ActorRef right) {
        Props.create(Philosopher, name, [left, right])
    }

    void onReceive(Object ignored) { }

    void preStart() {
        assert 2 == forks.size()
        while(true) {
            think()
            def forkFutures = [
                    ask(forks[0], new Take(), 1000),
                    ask(forks[1], new Take(), 1000)
            ]
            def messages = forkFutures.collect {
                Await.result(it, Duration.create(1, "seconds"))
            }
            if (messages.any { it in Rejected }) {
                println "$name : Oops, can't get my forks! Giving up."
                (0..1).each { num ->
                    if (messages[num] in Accepted) forks[num].tell(new Finished(), null)
                }
            } else {
                eat()
                forks.each { fork -> fork.tell(new Finished(), null) }
            }
        }
    }

    void think() {
        println "$name : I'm thinking"
        sleep random.nextInt(5000)
        println "$name : I'm done thinking"
    }

    void eat() {
        println "$name : I'm EATING"
        sleep random.nextInt(2000)
        println "$name : I'm done EATING"
    }
}

@Immutable class Fork extends UntypedActor {
    String name
    private boolean available = true

    static Props mkProps(String name) {
        Props.create(Fork, name)
    }

    void onReceive(message) {
        switch (message) {
            case Take:
                if (available) {
                    available = false
                    getSender().tell(new Accepted(), getSelf())
                } else {
                    getSender().tell(new Rejected(), getSelf())
                }
                break
            case Finished:
                assert !available
                available = true
                break
            default: throw new IllegalStateException("Cannot process the message: $message")
        }
    }
}

final class Take {}
final class Accepted {}
final class Rejected {}
final class Finished {}

def system = ActorSystem.create("Diner")
def forks = (1..5).collect{ system.actorOf(Fork.mkProps("Fork $it")) }
def philosophers = ['socrates ', 'plato    ', 'aristotle', 'descartes', 'nietzsche']
philosophers.eachWithIndex{ name, index ->
    system.actorOf(Philosopher.mkProps(name, forks[index], forks[(index + 1) % philosophers.size()]))
}
sleep 10000
system.shutdown()
