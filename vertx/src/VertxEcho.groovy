@Grab("io.vertx:vertx-core:2.0.2-final")
@GrabExclude("com.hazelcast:hazelcast")
import org.vertx.java.core.Handler
import org.vertx.java.core.net.NetSocket
import org.vertx.java.core.streams.Pump
import static org.vertx.java.core.VertxFactory.newVertx

def vertx = newVertx()

// create an embedded echo server
vertx.createNetServer().connectHandler(new Handler<NetSocket>() {
    void handle(NetSocket socket) {
        Pump.createPump(socket, socket).start()
    }
}).listen(1234)

sleep 1000

vertx.createNetClient().connect(1234, "localhost") { asyncResult ->
    if (asyncResult.succeeded()) {
        socket = asyncResult.result
        socket.dataHandler { buffer ->
            println "client receiving: $buffer"
        }
        5.times {
            String str = "hello $it\n"
            print "client sending: $str"
            socket.write str
            sleep 50
        }
    } else {
        println "Failed to connect $asyncResult.cause"
    }
}

sleep 2000
