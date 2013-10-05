import java.util.concurrent.ConcurrentHashMap
import groovy.transform.Immutable

class Util { static maybeWord(s) { s ? [s] : [] } }
import static Util.*

@Immutable class Chunk {
  String s
  public static final ZERO = new Chunk('')
  def plus(Chunk other) { new Chunk(s + other.s) }
  def plus(Segment other) { new Segment(s + other.l, other.m, other.r) }
  def flatten() { maybeWord(s) }
}

@Immutable class Segment {
  String l; List m; String r
  public static final ZERO = new Segment('', [], '')
  def plus(Chunk other) { new Segment(l, m, r + other.s) }
  def plus(Segment other) { new Segment(l, m + maybeWord(r + other.l) + other.m, other.r) }
  def flatten() { maybeWord(l) + m + maybeWord(r) }
}

def processChar(ch) { ch == ' ' ? new Segment('', [], '') : new Chunk(ch) }
static swords(s) { s.inject(Chunk.ZERO) { result, ch -> result + processChar(ch) } }

assert swords("Here is a sesquipedalian string of words").flatten() ==
        ['Here', 'is', 'a', 'sesquipedalian', 'string', 'of', 'words']

THREADS = 4

def pwords(s) {
  int n = (s.size() + THREADS - 1) / THREADS
  def map = new ConcurrentHashMap()
  (0..<THREADS).collect { i ->
    Thread.start {
      def (min, max) = [[s.size(), i * n].min(), [s.size(), (i + 1) * n].min()]
      map[i] = swords(s[min..<max])
    }
  }*.join()
  (0..<THREADS).collect { i -> map[i] }.sum().flatten()
}

assert pwords("Here is a sesquipedalian string of words") ==
        ['Here', 'is', 'a', 'sesquipedalian', 'string', 'of', 'words']
