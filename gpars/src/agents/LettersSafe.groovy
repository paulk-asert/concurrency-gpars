import groovyx.gpars.agent.Agent

def random = new Random()
def randomDelay = { sleep random.nextInt(10) }
def agent = new Agent<String>('')
('a'..'z').each { letter ->
  Thread.start{
    randomDelay()
    agent.send{ updateValue it + letter }
  }
}
sleep 100 // poor man's join
String result = agent.val
println result
println result.size()
