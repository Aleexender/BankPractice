import static org.junit.jupiter.api.Assertions.assertEquals;

import bank.Bank;
import bank.BankConcurrentService;
import bank.BankServiceProxy;
import bank.DefaultBankService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class DefaultBankServiceConcurrencyTest {

    private BankConcurrentService bankService;

    @BeforeEach
    void setUp() {
        Bank bank = new Bank();
        bankService = new BankServiceProxy(new DefaultBankService(bank));
        bankService.addAccount("John", 30, 1000);
    }

    @RepeatedTest(100)
     void testSimultaneousDeposits_shouldFailOnConcurrentDeposits() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(2);
        var startLatch = new CountDownLatch(1);
        var endLatch = new CountDownLatch(2);

        Runnable depositTask = () -> {
            try {
                startLatch.await();
                bankService.deposit("John", 500);
            } catch (Exception e) {

            } finally {
                endLatch.countDown();
            }
        };

        executorService.submit(depositTask); // 2번 동시에
        executorService.submit(depositTask);

        startLatch.countDown();
        endLatch.await();

        long finalBalance = bankService.checkBalance("John");
        assertEquals(1500, finalBalance);
    }

    @Test
    void testSimultaneousWithdrawals_shouldExecuteInOrder() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        Runnable withdrawTask = () -> {
            try {
                bankService.withdraw("John", 500);
            } finally {
                latch.countDown();
            }
        };

        executorService.submit(withdrawTask);
        executorService.submit(withdrawTask);

        latch.await();

        long finalBalance = bankService.checkBalance("John");
        assertEquals(0, finalBalance);  // 잔고는 0이어야 함
    }

    @Test
    void testDepositAndWithdraw_shouldExecuteInOrder() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        Runnable depositTask = () -> {
            try {
                bankService.deposit("John", 500);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        };

        Runnable withdrawTask = () -> {
            try {
                bankService.withdraw("John", 300);
            } finally {
                latch.countDown();
            }
        };

        // 입금과 출금 요청을 동시에 발생
        executorService.submit(depositTask);
        executorService.submit(withdrawTask);

        latch.await();

        // 입금과 출금 요청이 차례대로 처리되어야 함
        long finalBalance = bankService.checkBalance("John");
        assertEquals(1200, finalBalance);  // 잔고는 1200이어야 함 (1000 + 500 - 300)
    }
}
