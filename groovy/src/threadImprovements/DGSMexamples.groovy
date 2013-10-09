Thread.start{ sleep 1001; println 'one' }
Thread.start{ sleep 1000; println 'two' }


println 'one'
def t = Thread.start{ sleep 100; println 'three' }
println 'two'
t.join()
println 'four'
