package bank;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lock.AutoCloseableLock;

public class BankServiceProxy implements BankConcurrentService {
    private final BankConcurrentService bankConcurrentService;

    private final ConcurrentHashMap<String, Lock> accountLocks = new ConcurrentHashMap<>();

    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    public BankServiceProxy(BankConcurrentService bankConcurrentService) {
        this.bankConcurrentService = bankConcurrentService;
    }

    private Lock getLockForAccount(String name) {
        return accountLocks.computeIfAbsent(name, k -> new ReentrantLock());
    }

    @Override
    public long withdraw(String name, long asset) {
        Lock lock = getLockForAccount(name);
        try (AutoCloseableLock autoLock = new AutoCloseableLock(lock)) {
            return bankConcurrentService.withdraw(name, asset);
        }
    }

    @Override
    public long deposit(String name, long asset) throws IllegalAccessException {
        if(atomicInteger.incrementAndGet() == 2){
            throw new IllegalArgumentException();
        }
        var left = bankConcurrentService.deposit(name, asset);
        if (atomicInteger.decrementAndGet() == 2) {
            throw new IllegalArgumentException();
        }
        return left;
    }

    @Override
    public void addAccount(String name, int age, long asset) {
        bankConcurrentService.addAccount(name, age, asset);
    }

    @Override
    public long checkBalance(String name) {
        return bankConcurrentService.checkBalance(name);
    }
}
