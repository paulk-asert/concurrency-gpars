@Grab('net.java.quickcheck:quickcheck:0.6')
import static net.java.quickcheck.generator.CombinedGeneratorsIterables.someNonEmptyLists
import static net.java.quickcheck.generator.PrimitiveGenerators.nonEmptyStrings

def total = 0

someNonEmptyLists(nonEmptyStrings()).each { words ->
  def someSegment = words.size() > 2 ?
    new Segment(words[0], words[1..-2], words[-1]) :
    new Segment('', words, '')
  def expected = someSegment.flatten()
  assert expected == (someSegment + Chunk.ZERO).flatten()
  assert expected == (Chunk.ZERO + someSegment).flatten()
  assert expected == (someSegment + Segment.ZERO).flatten()
  assert expected == (Segment.ZERO + someSegment).flatten()
  total++
}

println "$total tests"
