package wordsplit

import groovy.transform.Immutable
import static wordsplit.Util.maybeWord

@Immutable class Chunk {
  String s
  static final ZERO = new Chunk('')
  def plus(Chunk other) { new Chunk(s + other.s) }
  def plus(Segment other) { new Segment(s + other.l, other.m, other.r) }
  def flatten() { maybeWord(s) }
}
