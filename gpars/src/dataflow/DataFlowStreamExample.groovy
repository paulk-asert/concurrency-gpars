import groovyx.gpars.dataflow.stream.DataflowStream
import static groovyx.gpars.dataflow.Dataflow.*

final streamA = new DataflowStream()
final streamB = new DataflowStream()
final streamX = new DataflowStream()
final streamY = new DataflowStream()
final results = new DataflowStream()

operator(inputs:  [streamA, streamB],
         outputs: [streamX, streamY]) { a, b ->
    streamX << a - b; streamY << a + b
}
operator(inputs:  [streamX, streamY],
         outputs: [results]) { x, y ->
    results << x * y
}

[[10, 20], [4, 5]].combinations().each{ thisA, thisB ->
    println thisA + ' ' + thisB
    task { streamA << thisA }
    task { streamB << thisB }
}
4.times { println results.val }
