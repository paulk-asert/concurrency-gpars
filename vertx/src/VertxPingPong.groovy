@Grab("io.vertx:vertx-core:2.0.2-final")
@GrabExclude("com.hazelcast:hazelcast")
import static org.vertx.java.core.VertxFactory.newVertx

def vertx = newVertx()
def eb = vertx.eventBus()

eb.registerHandler("ping-address") { message ->
    println "Received message: ${message.body()}"
    message.reply("pong!")
}

sleep 1000

vertx.setPeriodic(1000) {
    eb.send("ping-address", "ping!") { reply ->
        println "Received reply ${reply.body()}"
    }
}

sleep 5000
