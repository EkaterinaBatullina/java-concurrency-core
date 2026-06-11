package com.technokratos.reentrantlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class ReentrantLockTask {

    public static void main(String[] args) {
        Account firstAccount = new Account(1000);
        Account secondAccount = new Account(1000);

        Thread[] threads = {
                new Thread(() -> transfer(firstAccount, secondAccount, 100)),
                new Thread(() -> transfer(secondAccount, firstAccount, 200))
        };

        for (Thread thread : threads) thread.start();
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("FirstAccount balance: %s".formatted(firstAccount.getBalance()));
        System.out.println("SecondAccount balance: %s".formatted(secondAccount.getBalance()));
    }

    public static void transfer(Account source, Account target, int amount) {
        Lock sourceLock = source.getLock();
        Lock targetLock = target.getLock();

        while (true) {
            try {

                /*
                 * Пытаемся захватить оба lock'а с ограниченным временем ожидания.
                 *
                 * Если второй lock недоступен, освобождаем уже захваченный ресурс
                 * и повторяем попытку, избегая взаимной блокировки (deadlock).
                 */
                if (sourceLock.tryLock(50, TimeUnit.MILLISECONDS)) {
                    try {
                        if (targetLock.tryLock(50, TimeUnit.MILLISECONDS)) {
                            try {
                                if (source.getBalance() >= amount) {
                                    source.withdraw(amount);
                                    target.deposit(amount);
                                    System.out.println("Transfer of %s completed".formatted(amount));
                                } else {
                                    System.out.println("Insufficient funds for transfer");
                                }
                                return;
                            } finally {
                                targetLock.unlock();
                            }
                        }
                    } finally {
                        sourceLock.unlock();
                    }
                }

                /*
                 * Небольшая пауза перед повторной попыткой захвата lock'ов,
                 * чтобы избежать активного ожидания.
                 */
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}