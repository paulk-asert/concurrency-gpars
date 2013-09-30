import static groovyx.gpars.GParsPool.withPool
import groovyx.gpars.ParallelEnhancer

withPool {
    def oneStarters = (1..30).makeConcurrent()
        .collect { it ** 2 }
        .findAll { it ==~ '1.*' }
        .findAll { it ==~ '...' }
    assert oneStarters == [100, 121, 144, 169, 196]
}

def nums = 1..5
ParallelEnhancer.enhanceInstance(nums)
assert [1, 4, 9, 16, 25] == nums.collectParallel{ it * it }
