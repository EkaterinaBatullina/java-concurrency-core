package com.technokratos.readwritelock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockTask {

    /*
     * ReentrantReadWriteLock разделяет доступ
     * на операции чтения и записи.
     *
     * Несколько потоков могут одновременно читать данные,
     * если в данный момент не выполняется запись.
     *
     * Запись всегда эксклюзивна.
     */
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    /*
     * Имитация разделяемого ресурса.
     *
     * В реальных системах это может быть:
     * - кэш
     * - конфигурация приложения
     * - справочник
     * - набор настроек
     */
    private static String configuration = "v1";

    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            /*
             * Несколько потоков одновременно читают данные.
             *
             * ReadLock допускает конкурентное чтение,
             * поэтому все reader могут работать параллельно.
             */
            for (int i = 1; i <= 3; i++) {
                int readerId = i;

                executor.submit(() -> readConfiguration(readerId));
            }

            Thread.sleep(1000);

            /*
             * Поток записи получает эксклюзивный доступ.
             *
             * Пока удерживается WriteLock,
             * новые операции чтения и записи блокируются.
             */
            executor.submit(() -> updateConfiguration("v2"));

            Thread.sleep(500);

            /*
             * После завершения записи чтение снова
             * может выполняться параллельно.
             */
            for (int i = 4; i <= 6; i++) {
                int readerId = i;

                executor.submit(() -> readConfiguration(readerId));
            }

            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /*
     * Чтение данных.
     *
     * Одновременно этот метод могут выполнять
     * несколько потоков.
     */
    private static void readConfiguration(int readerId) {
        readLock.lock();
        try {
            System.out.printf("Reader-%d reading configuration: %s%n", readerId, configuration);

            Thread.sleep(2000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            readLock.unlock();
        }
    }

    /*
     * Обновление данных.
     *
     * WriteLock гарантирует эксклюзивный доступ.
     *
     * Пока выполняется запись:
     * - новые reader ждут
     * - другие writer тоже ждут
     */
    private static void updateConfiguration(String newValue) {
        writeLock.lock();
        try {
            System.out.println("Writer updating configuration...");

            Thread.sleep(1500);

            configuration = newValue;

            System.out.println("Configuration updated: " + configuration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            writeLock.unlock();
        }
    }
}