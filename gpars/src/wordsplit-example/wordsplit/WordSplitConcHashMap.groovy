import java.util.concurrent.ConcurrentHashMap
import static WordSplit.swords

THREADS = 4

def pwords(s) {
    int n = (s.size() + THREADS - 1) / THREADS
    def chm = new ConcurrentHashMap()
    (0..<THREADS).collect { i ->
        Thread.start {
            def (min, max) = [[s.size(), i * n].min(), [s.size(), (i + 1) * n].min()]
            chm[i] = swords(s[min..<max])
        }
    }*.join()
    (0..<THREADS).collect { i -> chm[i] }.sum().flatten()
}

assert pwords("Here is a sesquipedalian string of words") ==
        ['Here', 'is', 'a', 'sesquipedalian', 'string', 'of', 'words']
