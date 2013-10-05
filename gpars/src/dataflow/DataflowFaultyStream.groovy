import groovyx.gpars.dataflow.Dataflows
import static groovyx.gpars.dataflow.Dataflow.task

final flow = new Dataflows()
[10, 20].each { thisA ->
  [4, 5].each { thisB ->
    task { flow.a = thisA }
    task { flow.b = thisB }
    task { flow.x = flow.a - flow.b }
    task { flow.y = flow.a + flow.b }
    task { flow.result = flow.x * flow.y }
    println flow.result
  }
}
// => java.lang.IllegalStateException: A DataFlowVariable can only be assigned once.
