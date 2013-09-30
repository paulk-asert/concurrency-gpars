import static groovyx.gpars.GParsPool.withPool

withPool {
    def oneStarters = (1..30).parallel
        .map { it ** 2 }
        .filter { it ==~ '1.*' }
    assert oneStarters.collection == [1, 16, 100, 121, 144, 169, 196]

    // aggregations
    assert oneStarters.max() == 196
    assert oneStarters.reduce { a, b -> a + b } == 747
    assert oneStarters.sum() == 747
}
