import groovyx.gpars.agent.Agent

def random = new Random()
def randomDelay = { sleep random.nextInt(10) }
def agent = new Agent<String>('')
def threads = ('a'..'z').collect { letter ->
  Thread.start {
    randomDelay()
    agent.send{ updateValue it << letter }
  }
}
threads*.join()
String result = agent.val
println result
println result.size()
