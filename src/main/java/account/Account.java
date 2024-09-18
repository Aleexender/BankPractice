package account;


public class Account {
    private final String name;
    private final int age;
    private long asset;

    public Account(String name, int age, long asset) {
        this.name = name;
        this.age = age;
        this.asset = asset;
    }

    public long deposit(long amount) {
        return asset += amount;
    }

    public synchronized long withdraw(long amount) {
        if (this.asset < amount) {
            throw new IllegalArgumentException("low asset");
        }
        return asset -= amount;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public long getAsset() {
        return asset;
    }
}
