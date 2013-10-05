// serial
def a = 5 + 10 + 15
def b = 3 * 4 * 5
println "$a $b"

a = { 5 + 10 + 15 }.call()
b = { 3 * 4 * 5 }.call()
println "$a $b"

a = { 5 + 10 + 15 }()
def closureB = { 3 * 4 * 5 }
b = closureB()
println "$a $b"

// explicit threads
def c, d
def t1 = Thread.start { c = 5 + 10 + 15 }
def t2 = Thread.start { d = 3 * 4 * 5 }
t1.join()
t2.join()
println "$c $d"

// explicit threads
def list = []
t1 = Thread.start { synchronized(list){ list << 5 + 10 + 15 } }
t2 = Thread.start { synchronized(list){ list << 3 * 4 * 5 } }
t1.join()
t2.join()
println "${list[0]} ${list[1]}"

import java.util.concurrent.CopyOnWriteArrayList

// explicit threads
list = [] as CopyOnWriteArrayList
t1 = Thread.start { list.add(0, 5 + 10 + 15) }
t2 = Thread.start { list.add(1, 3 * 4 * 5) }
t1.join()
t2.join()
println "${list[0]} ${list[1]}"

import groovyx.gpars.extra166y.Ops
import groovyx.gpars.extra166y.ParallelArray
import jsr166y.ForkJoinPool
import static groovyx.gpars.GParsPool.withPool

// low-level parallel array
def executor = new ForkJoinPool(5)
def pa = ParallelArray.create(2, Integer, executor)
def ops = [{5 + 10 + 15},{3 * 4 * 5}]
def mapper = { ops[it].call() } as Ops.ObjectToInt
pa.replaceWithMappedIndex mapper
println "${pa.get(0)} ${pa.get(1)}"

// async closures
withPool {
    def futureE = { 5 + 10 + 15 }.callAsync()
    def futureF = { 3 * 4 * 5 }.callAsync()
    def e = futureE.get()
    def f = futureF.get()
    println "$e $f"
}

withPool {
    def asyncG = { 5 + 10 + 15 }.async()
    def asyncH = { 3 * 4 * 5 }.async()
    def futureG = asyncG()
    def futureH = asyncH()
    def g = futureG.get()
    def h = futureH.get()
    println "$g $h"
}

