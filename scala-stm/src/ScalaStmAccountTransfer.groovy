@Grab('org.scala-stm:scala-stm_2.10:0.7')
import scala.concurrent.stm.*
import static scala.concurrent.stm.japi.STM.*

class Account {
    private final Ref.View<Long> balance

    Account(long initial) {
        balance = newRef(initial)
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
atomic {
    println 'trying...'
    from.balance -= amount
    to.balance += amount
    sleep 500
}
println 'transfer success'
atomic { println "from: $from.balance, to: $to.balance" }

watcher.join()