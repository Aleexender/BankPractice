package bank;

public class DefaultBankService implements BankConcurrentService {

    private final Bank bank;


    public DefaultBankService(Bank bank) {
        this.bank = bank;
    }

    @Override
    public void addAccount(String name, int age, long asset) {
        bank.addAccount(name, age, asset);
    }


    @Override
    public long withdraw(String name, long asset) {
        var account = bank.findAccount(name);
        return account.withdraw(asset);
    }

    @Override
    public long deposit(String name, long asset) {
        var account = bank.findAccount(name);
        return account.deposit(asset);
    }

    @Override
    public long checkBalance(String name) {
        var account = bank.findAccount(name);
        return account.getAsset();
    }


}
