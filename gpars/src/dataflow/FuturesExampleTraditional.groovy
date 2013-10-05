// example adapted from Parallel Programming with .Net
def (f1, f2, f3, f4) = [{ sleep 1000; it }] * 3 + [{ x, y -> x + y }]

import static groovyx.gpars.GParsPool.withPool

withPool(2) {
    def a = 5

    def futureB
    def t = Thread.start{ futureB = f1(a) }
    def c = f2(a)
    def d = f3(c)
    t.join()
    def f = f4(futureB, d)

    assert f == 10
}
