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
* [GPars][1] dining philosophers (Reentrant lock, semaphore, synchronized, stm, csp, actor); primes (parallel, actors, dataflow); barber (semaphore, concurrent, stm, csp, actor, actor message handling)
* [Jetlang][2] dining philosophers
* [Gruple][3] dining philosophers
* [Akka Actors][4] dining philosophers (actors)
* [totallylazy][5] concurrent flatMap
* [Clojure Refs][6] account transfer (stm)
* [ScalaSTM][7] account transfer (stm)

Under construction ... more examples to appear soon ...

[1]: http://gpars.codehaus.org/ "GPars"
[2]: http://code.google.com/p/jetlang/ "Jetlang"
[3]: http://gruple.codehaus.org/ "Gruple"
[4]: http://doc.akka.io/docs/akka/2.2.1/java/untyped-actors.html "Akka Actors"
[5]: https://code.google.com/p/totallylazy/ "totallylazy"
[6]: http://clojure.org/refs "Clojure Refs"
[7]: http://nbronson.github.io/scala-stm/ "ScalaSTM"
