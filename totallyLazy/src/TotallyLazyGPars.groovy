@GrabResolver('http://repo.bodar.com')
@Grab('com.googlecode.totallylazy:totallylazy:1131')
import static groovyx.gpars.GParsExecutorsPool.withPool
import static com.googlecode.totallylazy.Callables.asString
import static com.googlecode.totallylazy.Sequences.sequence

withPool { pool ->
  assert ['5', '6'] == sequence(4, 5, 6)
      .drop(1)
      .mapConcurrently(asString(), pool)
      .toList()
}
