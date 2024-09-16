package bank;

import account.Account;
import java.util.ArrayList;
import java.util.List;

public class Bank {
    private final List<Account> accounts = new ArrayList<>();

    public void addAccount(String name, int age, long asset) {
        var account = new Account(name, age, asset);
        accounts.add(account);
    }

    public Account findAccount(String name) {
        return accounts.stream().
            filter(account -> account.getName().equals(name))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public long withdraw(String name, long asset) {
        var account = findAccount(name);
        return account.withdraw(asset);
    }

    public synchronized long deposit(String name, long asset) {
        var account = findAccount(name);
        return account.deposit(asset);
    }

    public long checkBalance(String name) {
        var account = findAccount(name);
        return account.getAsset();
    }

}
