@Grab('org.jetlang:jetlang:0.2.10')
import org.jetlang.fibers.ThreadFiber
import org.jetlang.channels.MemoryRequestChannel
import org.jetlang.channels.AsyncRequest

def req = new ThreadFiber() // or pool
def reply = new ThreadFiber()
def calcSum = new MemoryRequestChannel()
def calcAvg = new MemoryRequestChannel()
req.start()
reply.start()

calcSum.subscribe(reply) { it.reply(it.request.sum()) }
AsyncRequest.withOneReply(req, calcSum, [3, 4, 5]) { assert it == 12 }

calcAvg.subscribe(reply) { def nums = it.request; it.reply(nums.sum()/nums.size()) }
AsyncRequest.withOneReply(req, calcAvg, [3, 4, 5]) { assert it == 4 }

sleep 1000
req.dispose()
reply.dispose()
