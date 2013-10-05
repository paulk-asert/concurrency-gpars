package wordsplit

import static wordsplit.TestUtil.SAMPLES

def processChar(ch) { ch == ' ' ? new Segment('', [], '') : new Chunk(ch) }
def swords(s) { s.inject(Chunk.ZERO) { result, ch -> result + processChar(ch) }.flatten() }

assert swords("Here is a sesquipedalian string of words") ==
        ['Here', 'is', 'a', 'sesquipedalian', 'string', 'of', 'words']

SAMPLES.each { sentence, expectedWords -> assert swords(sentence) == expectedWords }
