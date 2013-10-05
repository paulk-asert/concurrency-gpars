package wordsplit

import static groovyx.gpars.GParsPool.withPool

THREADS = 4

def processChar(ch) { ch == ' ' ? new Segment('', [], '') : new Chunk(ch) }

def pwords(input) {
  withPool(THREADS) {
    input.collectParallel{ processChar(it) }.sumParallel().flatten()
  }
}

assert pwords("Here is a sesquipedalian string of words") ==
        ['Here', 'is', 'a', 'sesquipedalian', 'string', 'of', 'words']
