package Banking;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private final int id;
    private int balance;
    private final Lock lock = new ReentrantLock();

    public BankAccount(int id, int initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public int getId(){
        return  id;
    }
    public int getBalance() {
        // TODO: Consider locking (if needed)
        return balance;
    }

    public Lock getLock() {
        return lock;
    }

    public void deposit(int amount) {
        // TODO: Safely add to balance.
        lock.lock();
        try {
            balance += amount;
        } finally {
            lock.unlock();
        }

    }

    public void withdraw(int amount) {
        // TODO: Safely withdraw from balance.
        lock.lock();
        try {
            balance -= amount;
        } finally {
            lock.unlock();
        }
    }

    public void transfer(BankAccount target, int amount) {
        // TODO: Safely make the changes
        // HINT: Both accounts need to be locked, while the changes are being made
        // HINT: Be cautious of potential deadlocks.
        BankAccount firstLock , secondLock;
        if(this.id < target.id){
            firstLock = this;
            secondLock = target;
        }
        else {
            firstLock = target;
            secondLock = this;
        }
        firstLock.getLock().lock();
        secondLock.getLock().lock();

        try {
            this.balance -= amount;
            target.balance += amount;
        } finally {
            secondLock.getLock().unlock();
            firstLock.getLock().unlock();
        }

    }
}
