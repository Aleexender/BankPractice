package bank;

public interface BankConcurrentService {
    long withdraw(String name, long asset);

    long deposit(String name, long asset) throws IllegalAccessException;

    void addAccount(String name, int age, long asset);

    long checkBalance(String name);
}
