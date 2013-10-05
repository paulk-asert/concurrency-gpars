package wordsplit

import groovy.transform.Immutable
import static wordsplit.Util.maybeWord

@Immutable class Segment {
  String l; List m; String r
   static final ZERO = new Segment('', [], '')
  def plus(Chunk other) { new Segment(l, m, r + other.s) }
  def plus(Segment other) {
    new Segment(l, m + maybeWord(r + other.l) + other.m, other.r)
  }
  def flatten() { maybeWord(l) + m + maybeWord(r) }
}
