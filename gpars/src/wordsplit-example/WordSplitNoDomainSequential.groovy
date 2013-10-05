import static wordsplit.TestUtil.SAMPLES

def swords = { s ->
    def result = []
    def word = ''
    s.each{ ch ->
        if (ch == ' ') {
            if (word) result += word
            word = ''
        } else word += ch
    }
    if (word) result += word
    result
}

SAMPLES.each { sentence, expectedWords -> assert swords(sentence) == expectedWords }
