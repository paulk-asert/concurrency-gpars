import static groovyx.gpars.GParsPool.withPool

withPool {
    def oneStarters = (1..30)
        .collectParallel { it ** 2 }
        .findAllParallel { it ==~ '1.*' }
    assert oneStarters == [1, 16, 100, 121, 144, 169, 196]


    // aggregations
    assert oneStarters.maxParallel() == 196
    assert oneStarters.sumParallel() == 747
}
