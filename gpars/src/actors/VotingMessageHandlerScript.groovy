import static groovyx.gpars.actor.Actors.*

def votes = messageHandler {
    def processVote = { language ->
        if (language.count('o') == 2) println "You voted for $language"
        else println 'Sorry, please try again'
    }
    when { String language -> processVote(language) }
    when { List languages -> languages.each{ processVote it } }
}

votes << 'Groovy'
votes << 'Boo'
votes << 'Go'
votes << ['Groovy', 'Fortran', 'Cobol']

votes.stop()
votes.join()
