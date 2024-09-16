import static org.junit.jupiter.api.Assertions.assertEquals;

import bank.Bank;
import bank.BankOperationGuard;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public class BankOperationGuardTest {

    private BankOperationGuard bankOperationGuard;
    private Bank bank;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        bank.addAccount("John", 30, 1000);
        bankOperationGuard = new BankOperationGuard(bank);
    }

    @RepeatedTest(100)
    void two_deposit_will_fail() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(2);

        Runnable depositTask = () -> {
            try {
                bankOperationGuard.deposit("John", 500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        executorService.submit(depositTask,depositTask);


        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        long finalBalance = bank.checkBalance("John");
        assertEquals(2000, finalBalance);
    }

    @RepeatedTest(100)
    void two_withdraw_will_be_processed_in_order() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(2);

        Runnable withdrawTask1 = () -> bankOperationGuard.withdraw("John", 500);
        Runnable withdrawTask2 = () -> bankOperationGuard.withdraw("John", 400);


        executorService.submit(withdrawTask1);
        executorService.submit(withdrawTask2);


        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        var finalBalance = bank.checkBalance("John");

        assertEquals(100, finalBalance);
    }

}
