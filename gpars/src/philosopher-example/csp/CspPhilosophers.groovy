package csp
// inspired by similar examples at the web sites below:
// http://www.cs.kent.ac.uk/projects/ofa/jcsp/
// http://www.soc.napier.ac.uk/~jmk/#_Toc271192596
@Grab('org.codehaus.jcsp:jcsp:1.1-rc5')
import org.jcsp.lang.CSProcess
import org.jcsp.lang.ChannelInput
import org.jcsp.lang.ChannelOutput
import org.jcsp.lang.Channel
import groovyx.gpars.csp.PAR
import groovyx.gpars.csp.ALT
import static java.lang.System.currentTimeMillis

def names = ['socrates', 'plato', 'aristotle', 'descartes', 'nietzsche']
enum ForkAction { Take, Release, Stop }
import static ForkAction.*

class Philosopher implements CSProcess {
    ChannelOutput leftFork, rightFork
    private random = new Random()
    String name
    private meals = 0
    def forks = []
    def start = currentTimeMillis()

    void run() {
        while (currentTimeMillis() - start < 10000) {
            think()
            eat()
        }
        [leftFork, rightFork].each { it.write(Stop) }
        println toString()
    }

    void think() {
        println "$name is thinking"
        sleep random.nextInt(5000)
    }

    void eat() {
        [leftFork, rightFork].each { it.write(Take) }
        println "$name is EATING"
        meals++
        sleep random.nextInt(3000)
        [leftFork, rightFork].each { it.write(Release) }
    }

    String toString() {
        switch (meals) {
            case 0: return "$name has starved"
            case 1: return "$name has eaten once"
            default: return "$name has eaten $meals times"
        }
    }
}

class Fork implements CSProcess {
    ChannelInput left, right
    private active = [0, 1] as Set

    void run() {
        def fromPhilosopher = [left, right]
        def forkAlt = new ALT(fromPhilosopher)
        while (active) {
            def i = forkAlt.select()
            read fromPhilosopher, i, Take
            read fromPhilosopher, i, Release
        }
    }

    void read(phil, index, expected) {
        if (!active.contains(index)) return
        def m = phil[index].read()
        if (m == Stop) active -= index
        else assert m == expected
    }
}

def lefts = Channel.createOne2One(names.size())
def rights = Channel.createOne2One(names.size())

def philosophers = (0..<names.size()).collect { i ->
    return new Philosopher(leftFork: lefts[i].out(),
            rightFork: rights[i].out(),
            name: names[i])
}

def forks = (0..<names.size()).collect { i ->
    return new Fork(left: lefts[i].in(),
            right: rights[(i + 1) % names.size()].in())
}

new PAR(philosophers + forks).run()
