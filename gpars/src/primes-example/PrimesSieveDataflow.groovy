// adapted from GPars example, repo: http://git.codehaus.org/gitweb.cgi?p=gpars.git
// file:src/test/groovy/groovyx/gpars/samples/dataflow/DemoSieveEratosthenesTheGoWay.groovy
import groovyx.gpars.dataflow.DataflowQueue
import static groovyx.gpars.dataflow.Dataflow.task as go

def generate(channel) {
    {-> (2..100).each { channel << it } }
}

def filter(inChannel, outChannel, def prime) {
    {->
        while (true) {
            def number = inChannel.val
            if (number % prime != 0) {
                outChannel << number
            }
        }
    }
}

def channel = new DataflowQueue()
go generate(channel)
(1..25).each {
    def prime = channel.val
    println "Found $prime"
    def next = new DataflowQueue()
    go filter(channel, next, prime)
    channel = next
}
