import groovyx.gpars.activeobject.ActiveObject
import groovyx.gpars.activeobject.ActiveMethod

@ActiveObject
class VotingActiveObject {
    @ActiveMethod
    def vote(String language) {
        processVote(language)
    }

    @ActiveMethod
    def vote(List<String> languages) {
        languages.collect{ processVote it }
    }

    private processVote(language) {
        if (language.size() == 6)
            "You voted for $language"
        else
            'Sorry, please try again'
    }
}

def voter = new VotingActiveObject()
def result1 = voter.vote('Scala')
def result2 = voter.vote('Groovy')
def result3 = voter.vote(['Pascal', 'Clojure', 'Groovy'])
[result1.get(), result2.get(), *result3.get()].each{ println it }
