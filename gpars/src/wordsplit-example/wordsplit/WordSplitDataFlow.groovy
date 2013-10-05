package wordsplit

import static groovyx.gpars.dataflow.Dataflow.task
import groovyx.gpars.dataflow.Dataflows

def processChar(ch) { ch == ' ' ? new Segment('', [], '') : new Chunk(ch) }
def swords(s) { s.inject(Chunk.ZERO) { result, ch -> result + processChar(ch) } }
THREADS = 4
def partition(s, n, i) { s[[s.size(), i * n].min()..<[s.size(), (i + 1) * n].min()] }

def pwords(s) {
  int n = (s.size() + THREADS - 1) / THREADS
  new Dataflows().with {
    task { a = swords(partition(s, n, 0)) }
    task { b = swords(partition(s, n, 1)) }
    task { c = swords(partition(s, n, 2)) }
    task { d = swords(partition(s, n, 3)) }
    task { sum1 = a + b }
    task { sum2 = c + d }
    task { sum = sum1 + sum2 }
//        task { sum = a + b + c + d }
    sum
  }.flatten()
}

assert pwords("Here is a sesquipedalian string of words") ==
        ['Here', 'is', 'a', 'sesquipedalian', 'string', 'of', 'words']
