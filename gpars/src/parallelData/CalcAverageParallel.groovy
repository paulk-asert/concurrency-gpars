//@Grab('org.codehaus.gpars:gpars:0.10')
import static groovyx.gpars.GParsPool.withPool

def nums = 0..10000
withPool(5) {
    println nums.sumParallel() / nums.size()
}
