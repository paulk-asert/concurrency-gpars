import groovyx.gpars.dataflow.Dataflows
import static groovyx.gpars.dataflow.Dataflow.task

final flow = new Dataflows()
task { flow.result = flow.x + flow.y }
task { flow.x = 10 }
task { flow.y =  5 }
assert 15 == flow.result

new Dataflows().with {
    task { result = a * b }
    task { b = c + 10 }
    task { c =  2 }
    task { a =  5 }
    assert 60 == result
}

println 'done'
