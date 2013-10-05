package wordsplit

import static groovyx.gpars.GParsPool.withPool

THREADS = 4

def processChar(ch) { ch == ' ' ? new Segment('', [], '') : new Chunk(ch) }
def swords = { s -> s.inject(Chunk.ZERO) { result, ch -> result + processChar(ch) } }

THRESHHOLD = 10

def partition(piece) {
  piece.size() <= THRESHHOLD ? piece :
    [piece[0..<THRESHHOLD]] + partition(piece.substring(THRESHHOLD))
}

def pwords = { input ->
  withPool(THREADS) {
    partition(input).parallel.map(swords).sum().flatten()
  }
}

assert pwords("Here is a sesquipedalian string of words") ==
        ['Here', 'is', 'a', 'sesquipedalian', 'string', 'of', 'words']
