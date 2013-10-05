import static groovyx.gpars.actor.Actors.*

def upper = reactor { code -> code.toUpperCase() }
def hello = reactor { println "Hello $it!" }

def main = actor {
  upper 'world'
  react { result ->
    hello result
  }
}

main.join()
hello.stop()
hello.join()
