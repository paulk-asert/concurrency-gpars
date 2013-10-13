concurrency-gpars
=================

Source code for the following talk:

http://www.slideshare.net/paulk_asert/concurrency-gpars

It is mainly an illustration of the [GPars][1] concurrency library for the JVM but also contains some bonus examples of other frameworks for illustrative purposes.

GPars examples:
* Data Parallelism
* Agents
* Actors
* Dataflow
* Fork join
* Async Closures
* Software Transactional Memory
* Word split example (Sequential, ConcurrentHashMap, Dataflow, DataParallelism, ForkJoin, Agent, MapReduce, property test)

Bonus examples exist for the following libraries:
* [Groovy][2] built-in improvements over Java (@Synchronized, @Lazy, @WithReadLock, @WithWriteLock, thread and process improvements)
* [GPars][1]
  * dining philosophers (Reentrant lock, semaphore, synchronized, stm, csp, actor, ThreadMXBean deadlock detection, JCarder deadlock detection)
  * primes (parallel, actors, dataflow)
  * barber (semaphore, concurrent, stm, csp, actor, actor message handling)
* [Jetlang][3] dining philosophers
* [Gruple][4] dining philosophers
* [Akka Actors][5] dining philosophers (actors)
* [totallylazy][6] concurrent flatMap
* [Clojure Refs][7] account transfer (stm)
* [ScalaSTM][8] account transfer (stm)
* [Vert.x][9] dining philosophers (actor-like verticles)

Under construction ... more examples to appear soon ...

[1]: http://gpars.codehaus.org/ "GPars"
[2]: http://groovy.codehaus.org/ "Groovy"
[3]: http://code.google.com/p/jetlang/ "Jetlang"
[4]: http://gruple.codehaus.org/ "Gruple"
[5]: http://doc.akka.io/docs/akka/2.2.1/java/untyped-actors.html "Akka Actors"
[6]: https://code.google.com/p/totallylazy/ "totallylazy"
[7]: http://clojure.org/refs "Clojure Refs"
[8]: http://nbronson.github.io/scala-stm/ "ScalaSTM"
[9]: http://vertx.io/ "Vert.x"
