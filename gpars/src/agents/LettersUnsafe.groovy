

def random = new Random()
def randomDelay = { sleep random.nextInt(10) }
String result = ''
('a'..'z').each { letter ->
  Thread.start{
    randomDelay()
    result += letter
  }
}
sleep 100 // poor man's join

println result
println result.size()
