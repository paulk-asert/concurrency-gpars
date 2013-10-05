def isPrime = { x -> ! (2..<x).any { y -> x % y == 0 } }

groovyx.gpars.GParsPool.withPool {
    def nums = 2..100
//    println nums.toList().countParallel{ isPrime(it) }
//    println nums.countParallel(isPrime)
//    def result = (2..100).makeConcurrent().count(isPrime)
    def result = nums.countParallel()//    { println isPrime(it); isPrime(it) }
    println result
}
