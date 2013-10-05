import groovy.transform.Immutable
import static groovyx.gpars.actor.Actors.messageHandler
import static java.util.concurrent.TimeUnit.SECONDS

trader = messageHandler {
    when { Buy message ->
        println "Buying $message.quantity shares of $message.name" }
    when { Sell message ->
        println "Selling $message.quantity shares of $message.name" }
}

@Immutable class Buy { String name; int quantity }
@Immutable class Sell { String name; int quantity }

trader << new Buy("GOOG", 1000)
trader << new Sell("AAPL", 500)

trader.join(1, SECONDS)
