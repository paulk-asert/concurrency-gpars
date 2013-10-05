// example adapted from Parallel Programming with .Net
def (f1, f2, f3, f4) = [{ sleep 1000; it }] * 3 + [{ x, y -> x + y }]

def a = 5

def b = f1(a)
def c = f2(a)
def d = f3(c)
def f = f4(b, d)

assert f == 10
