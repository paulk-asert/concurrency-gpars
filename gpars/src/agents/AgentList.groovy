import groovyx.gpars.agent.Agent

def speakers = new Agent<List>(['Alex'], {it?.clone()})  //add Alex

speakers.send {updateValue it << 'Hilary'}               //add Hilary

final Thread t1 = Thread.start {
    speakers {updateValue it << 'Ken'}                   //add Ken
}

final Thread t2 = Thread.start {
    speakers << {updateValue it << 'Guy'}                //add Guy
    speakers << {updateValue it << 'Ralph'}              //add Ralph
}

[t1, t2]*.join()
assert speakers.val as Set ==
    ['Alex', 'Hilary', 'Ken', 'Guy', 'Ralph'] as Set

// advanced  cases below:

def log = []
speakers.addListener {_, newValue ->
  log << "The new speaker lineup is: $newValue"}
speakers.addValidator {oldValue, newValue ->
  if (!(newValue - oldValue)[0].contains('n'))
    throw new IllegalArgumentException('bad name') }

['Douglas', 'Jenny', 'Martin', 'Rich'].each { next ->
  speakers { updateValue it << next }
}

speakers.await()
assert speakers.errors*.class.name ==
    ['java.lang.IllegalArgumentException', 'java.lang.IllegalArgumentException']

speakers.send { throw new IllegalStateException("Oh No") }
assert speakers.val as Set ==
    ['Alex', 'Hilary', 'Ken', 'Guy', 'Ralph', 'Jenny', 'Martin'] as Set
assert speakers.errors*.class.name == ['java.lang.IllegalStateException']
assert log.join('\n') == '''\
The new speaker lineup is: [Alex, Hilary, Ken, Guy, Ralph, Jenny]
The new speaker lineup is: [Alex, Hilary, Ken, Guy, Ralph, Jenny, Martin]'''