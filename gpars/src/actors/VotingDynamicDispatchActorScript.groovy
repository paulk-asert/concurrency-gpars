import groovyx.gpars.actor.DynamicDispatchActor

class VotingActor extends DynamicDispatchActor {
    void onMessage(String language) {
        processVote(language)
    }

    void onMessage(List languages) {
        languages.each{ processVote it }
    }

    private processVote(language) {
        if (language.startsWith('G'))
            println "You voted for $language"
        else
            println 'Sorry, please try again'
    }
}

final votes = new VotingActor().start()

votes << 'Groovy'
votes << 'C++'
votes << ['Groovy', 'Go', 'Dart']
votes.stop()
votes.join()
