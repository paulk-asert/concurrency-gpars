import groovyx.gpars.dataflow.Dataflows
import static groovyx.gpars.dataflow.Dataflow.task

final flow = new Dataflows()
task { flow.a = 10 }
task { flow.b =  5 }
task { flow.x = flow.a - flow.b }
task { flow.y = flow.a + flow.b }
task { flow.result = flow.x * flow.y }
assert flow.result == 75
