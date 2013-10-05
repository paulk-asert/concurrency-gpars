import static groovyx.gpars.actor.Actors.*

def votes = reactor {
    it.endsWith('y') ?
        "You voted for $it" :
        "Sorry, please try again"
}

println votes.sendAndWait('Groovy')
println votes.sendAndWait('JRuby')
println votes.sendAndWait('Go')

def languages = ['Groovy', 'Dart', 'C++']

def booth = actor {
    languages.each{ votes << it }
    loop {
        languages.size().times {
            react {
                println it
            }
        }
        stop()
    }
}

booth.join()
votes.stop()
votes.join()
