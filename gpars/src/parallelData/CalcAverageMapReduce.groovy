//@Grab('org.codehaus.gpars:gpars:0.10')
import static groovyx.gpars.GParsPool.withPool

def nums = 0..10000
withPool(5) {
    def ans = nums
            .parallel
            .map{ [it, 1] }
            .reduce{ a, b -> [a[0] + b[0], a[1] + b[1]] }
    println ans[0] / ans[1]
}
