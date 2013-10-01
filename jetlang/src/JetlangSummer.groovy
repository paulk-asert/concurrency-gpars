@Grab('org.jetlang:jetlang:0.2.10')
import org.jetlang.fibers.ThreadFiber
import org.jetlang.channels.MemoryRequestChannel
import org.jetlang.channels.AsyncRequest

def req = new ThreadFiber() // or pool
def reply = new ThreadFiber()
def channel = new MemoryRequestChannel()
req.start()
reply.start()

channel.subscribe(reply) { it.reply(it.request.sum()) }
AsyncRequest.withOneReply(req, channel, [3, 4, 5]) { println it }

sleep 1000
req.dispose()
reply.dispose()
