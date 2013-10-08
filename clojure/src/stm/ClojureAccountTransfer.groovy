@Grab('org.clojure:clojure:1.3.0')
import clojure.lang.Ref
import static clojure.lang.LockingTransaction.runInTransaction as atomic

class Account {
    private final Ref balanceRef

    Account(long initial) {
        balanceRef = new Ref(initial)
    }

    void setBalance(long newBalance) {
        if (newBalance < 0) throw new RuntimeException("not enough money")
        balanceRef.set newBalance
    }

    long getBalance() { balanceRef.deref() }
}

def from = new Account(20)
def to = new Account(20)
def amount = 10

def watcher = Thread.start {
    15.times {
        atomic {
            println "from: ${from.balance} to: ${--to.balance}"
        }
        sleep 100
    }
}

sleep 150
try {
    atomic {
        println 'about to transfer ...'
        from.balance -= amount
        to.balance += amount
        sleep 500
    }
    println 'transfer success'
} catch (all) {
    println all.message
}
atomic {
    println "from: $from.balance, to: $to.balance"
}

watcher.join()
