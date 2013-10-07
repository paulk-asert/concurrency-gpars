import static groovyx.gpars.GParsPool.withPool

def isPrime = { x -> ! (2..<x).any { y -> x % y == 0 } }
def nums = 2..100

withPool {
    println nums.findAllParallel{ isPrime(it) }
    assert nums.countParallel{ isPrime(it) } == 25
}
