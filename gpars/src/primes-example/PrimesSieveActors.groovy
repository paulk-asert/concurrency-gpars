// adapted from GPars example, repo: http://git.codehaus.org/gitweb.cgi?p=gpars.git
// file:src/test/groovy/groovyx/gpars/samples/actors/dda/DemoSieveEratosthenesActors.groovy
import groovyx.gpars.actor.DynamicDispatchActor

def chain = new FilterBy(2).start()

(2..100).each { chain it }
chain.sendAndWait 'Poisson'

final class FilterBy extends DynamicDispatchActor {
    int myPrime
    private next

    def FilterBy(myPrime) {
        this.myPrime = myPrime
        println "Found $myPrime"
    }

    def onMessage(int value) {
        if (value % myPrime != 0) {
            if (next) next value // pass on to next in chain
            else next = new FilterBy(value).start()
        } // else discard non-prime
    }

    def onMessage(def poisson) {
        if (next) {
            def replyTo = sender
            next.sendAndContinue(poisson) {
                stop()
                replyTo?.send('Done')
            }
        } else {
            stop()
            reply 'Done'
        }
    }
}