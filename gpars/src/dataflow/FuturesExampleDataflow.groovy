def (f1, f2, f3, f4) = [{ sleep 1000; it }] * 3 +
                       [{ x, y -> x + y }]

import groovyx.gpars.dataflow.Dataflows
import static groovyx.gpars.dataflow.Dataflow.task

new Dataflows().with {
    task { a = 5 }
    task { b = f1(a) }
    task { c = f2(a) }
    task { d = f3(c) }
    task { f = f4(b, d) }
    assert f == 10
}

