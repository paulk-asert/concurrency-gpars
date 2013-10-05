package wordsplit

import static groovyx.gpars.GParsPool.runForkJoin
import static groovyx.gpars.GParsPool.withPool

THRESHHOLD = 10
THREADS = 4

def processChar(ch) { ch == ' ' ? new Segment('', [], '') : new Chunk(ch) }
def swords(s) { s.inject(Chunk.ZERO) { result, ch -> result + processChar(ch) } }

def pwords(input) {
  withPool(THREADS) {
    runForkJoin(0, input.size(), input) { first, last, s ->
      def size = last - first
      if (size <= THRESHHOLD) {
        swords(s[first..<last])
      } else { // divide and conquer
        int pivot = first + size / 2
        forkOffChild(first, pivot, s)
        forkOffChild(pivot, last, s)
        childrenResults.sum()
      }
    }.flatten()
  }
}

assert pwords("Here is a sesquipedalian string of words") ==
        ['Here', 'is', 'a', 'sesquipedalian', 'string', 'of', 'words']
