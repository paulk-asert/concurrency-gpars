package wordsplit

import groovyx.gpars.agent.Agent

def processChar(ch) { ch == ' ' ? new Segment('', [], '') : new Chunk(ch) }

def swords(s) { s.inject(Chunk.ZERO) { result, ch -> result + processChar(ch) } }

THREADS = 4
def pwords(s) {
  int n = (s.size() + THREADS - 1) / THREADS
  def agent = new Agent<Map>([:], {it?.clone()})
  (0..<THREADS).collect { i ->
    Thread.start {
      def (min, max) = [[s.size(), i * n].min(), [s.size(), (i + 1) * n].min()]
      agent << { it[i] = swords(s[min..<max]); updateValue it }
    }
  }*.join()
  assert agent.val instanceof LinkedHashMap
  (0..<THREADS).collect { i -> agent.val[i] }.sum().flatten()
}

100.times {
  assert pwords("Here is a sesquipedalian string of words") ==
        ['Here', 'is', 'a', 'sesquipedalian', 'string', 'of', 'words']
}
