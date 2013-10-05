package actor
// adapted from GPars example
// repo: http://git.codehaus.org/gitweb.cgi?p=gpars.git
// file: src/test/groovy/groovyx/gpars/samples/actors/DemoDiningPhilosophers.groovy
import groovyx.gpars.actor.DefaultActor
import groovyx.gpars.actor.Actors
import groovy.beans.Bindable
import groovy.transform.Immutable

def names = ['socrates', 'plato', 'aristotle', 'descartes', 'nietzsche']
Actors.defaultActorPGroup.resize names.size()

class Philosopher extends DefaultActor {
    private random = new Random()
    private haveFork = {it instanceof Accepted}
    private meals = 0
    String name
    def forks
    @Bindable String status

    void act() {
        assert 2 == forks.size()
        loop {
            think()
            forks.eachWithIndex{ fork, i  -> fork.send new Take(i) }
            react {a ->
                react {b ->
                    if ([a, b].every(haveFork)) eat()
                    [a, b].findAll(haveFork).each {
                        forks[it.index].send new Release(it.index)
                    }
                }
            }
        }
    }

    void think() {
        setStatus('thinking')
        sleep random.nextInt(4000)
        setStatus('')
    }

    void eat() {
        setStatus("eating ${++meals}")
        sleep random.nextInt(4000)
        setStatus('')
    }

    String toString() {
        switch (meals) {
            case 0: return "$name has starved"
            case 1: return "$name has eaten once"
            default: return "$name has eaten $meals times"
        }
    }
}

class Fork extends DefaultActor {
    String name
    boolean available = true

    void act() {
        loop {
            react {message ->
                def i = message?.index
                switch (message) {
                    case Take:
                        if (available) {
                            available = false
                            reply new Accepted(i)
                        }
                        else reply new Rejected(i)
                        break
                    case Release:
                        assert !available
                        available = true
                        break
                    default: throw new IllegalStateException("Cannot process the message: $message")
                }
            }
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

import groovy.swing.*
import java.awt.Font
import static javax.swing.JFrame.*

def frame = new SwingBuilder().frame(title: 'Philosophers', defaultCloseOperation: EXIT_ON_CLOSE) {
    vbox {
        hbox {
            (0..<names.size()).each { i ->
                def widget = textField(id: names[i], text: names[i].center(14))
                widget.font = new Font(widget.font.name, widget.font.style, 36)
                philosophers[i].propertyChange = { widget.text = philosophers[i].status.center(14) }
            }
        }
    }
}

frame.pack()
frame.visible = true
forks*.start()
sleep 5000
philosophers*.start()

sleep 20000

forks*.stop()
forks*.join()
philosophers*.stop()
philosophers*.join()
frame.dispose()
philosophers.each { println it }
