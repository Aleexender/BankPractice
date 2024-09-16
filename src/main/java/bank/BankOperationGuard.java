package bank;

import account.Account;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankOperationGuard {
    private final Bank bank;
    private final Lock lock = new ReentrantLock();
    /*todo
        과연 어떤걸 잡아야하는가? -> BlockingQueue<Account> 로 잡아도 되는가? Runnable로 잡아야하는건가?
        블로킹 큐를 여기에다가 구현하는게 맞는가? -> 맞는거 같긴한데...
     */
    private final BlockingQueue<Account> queue = new ArrayBlockingQueue<>(3,true);
    private final BlockingQueue<Long> operationQueue = new ArrayBlockingQueue<>(3, true); // update를 하나만 할수있는 애를 만들어라


    public BankOperationGuard(Bank bank) {
        this.bank = bank;
    }

    public long deposit(String name, long asset) throws IllegalAccessException {
        if (lock.tryLock()) {
            try {
                var account = bank.findAccount(name);
                return account.deposit(asset);
            } finally {
                lock.unlock();
            }
        } else {
            throw new IllegalAccessException("다른 스레드가 이미 락을 획득");
        }
    }

    public void withdraw(String name, long asset) {
        var account = bank.findAccount(name);
        queue.add(account);
        operationQueue.add(asset);

        try {
            Long take = operationQueue.take();
            account.withdraw(take);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
