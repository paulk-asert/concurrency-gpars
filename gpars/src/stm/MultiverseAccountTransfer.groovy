import org.multiverse.api.references.TxnLong
import static groovyx.gpars.stm.GParsStm.atomic
import static org.multiverse.api.StmUtils.newTxnLong

class Account {
    private final TxnLong balance

    Account(long initial) {
        balance = newTxnLong(initial)
    }

    void setBalance(long newBalance) {
        if (newBalance < 0) throw new RuntimeException("not enough money")
        balance.set newBalance
    }

    long getBalance() {
        balance.get()
    }
}

def from = new Account(20)
def to = new Account(20)
def amount = 10

def watcher = Thread.start {
    15.times {
        atomic { println "from: ${from.balance}, to: ${--to.balance}" }
        sleep 100
    }
}

sleep 150
try {
    atomic {
        println 'trying...'
        from.balance -= amount
        to.balance += amount
        sleep 500
    }
    println 'transfer success'
} catch(all) {
    println all.message
}
atomic { println "from: $from.balance, to: $to.balance" }

watcher.join()
