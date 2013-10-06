@GrabResolver('http://repo.bodar.com/')
@Grab('com.googlecode.totallylazy:totallylazy:1131')
import static com.googlecode.totallylazy.Sequences.flatMapConcurrently
import static com.googlecode.totallylazy.numbers.Numbers.*

assert flatMapConcurrently(range(6, 10)) {
    even(it) ? [it, it * it] : []
}.toList() == [6, 36, 8, 64, 10, 100]
