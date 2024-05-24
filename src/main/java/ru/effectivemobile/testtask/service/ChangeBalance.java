package ru.effectivemobile.testtask.service;

import ru.effectivemobile.testtask.model.Account;
import ru.effectivemobile.testtask.repository.AccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Класс для работы с балансом.
 */
public class ChangeBalance extends TimerTask {

    public static final Object monitor = new Object();

    private static final int DELAY_OR_PERIOD = 60000;
    private static final int NUMBER_TO_CONVERT = 100;
    private static final int SCALE = 2;
    private static final double PERCENT = 1.05;
    private static final double MAXIMUM_PERCENTAGE = 3.07;

    private Double balance;
    private final Double initialBalance;

    private final AccountRepository accountRepository;
    private final Account account;

    public ChangeBalance(Double balance, Account account, AccountRepository accountRepository) {
        this.balance = balance;
        this.initialBalance = balance;
        this.accountRepository = accountRepository;
        this.account = account;
    }

    /**
     * Метод для округления баланса до двух знаков после запятой.
     * @return баланс с двумя знаками после запятой.
     */
    public static Double round(Double balance) {
        return BigDecimal.valueOf(balance)
                .setScale(SCALE, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Перевод баланса в копейки для хранения в базе данных.
     * @return баланс в копейках.
     */
    public static Long toLong(Double balance) {
        balance *= NUMBER_TO_CONVERT;
        return balance.longValue();
    }

    /**
     * Перевод баланса в рубли.
     * @return баланс в рублях.
     */
    public static Double toDouble(Long balance) {
        return (double) balance / NUMBER_TO_CONVERT;
    }

    /**
     * Метод для изменения баланса на определенный процент через указанный промежуток времени.
     * @param account аккаунт пользователя.
     * @param repository репозиторий для работы с аккаунтом.
     */
    public static void percentageIncrease(Double balance, Account account,
                                            AccountRepository repository) {

        Thread threadChangeBalance = new Thread(() -> {
            try {
                Timer timer = new Timer();

                ChangeBalance changeBalance = new ChangeBalance(balance, account, repository);

                timer.schedule(changeBalance, DELAY_OR_PERIOD, DELAY_OR_PERIOD);

                System.out.println("Start");

                synchronized (monitor) {
                    monitor.wait();
                    timer.cancel();
                    System.out.println(timer.purge());
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        threadChangeBalance.start();
    }

    /**
     * Логика увелечения баланса на определенный процент.
     */
    @Override
    public void run() {

        balance = balance * PERCENT;
        account.setBalance(toLong(round(balance)));

        System.out.println(balance);

        if (balance > initialBalance * MAXIMUM_PERCENTAGE){
            synchronized (monitor){
                monitor.notify();
            }
        }

        accountRepository.save(account);
    }
}
