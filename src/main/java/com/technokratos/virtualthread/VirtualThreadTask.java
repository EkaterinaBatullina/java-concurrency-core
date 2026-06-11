package com.technokratos.virtualthread;

import java.util.concurrent.locks.ReentrantLock;

public class VirtualThreadTask {
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {

        /*
         * Демонстрация масштабирования виртуальных потоков:
         * создаётся большое количество (10_000) параллельных задач.
         *
         * В отличие от платформенных потоков, виртуальные потоки
         * дешёвы по памяти и позволяют запускать тысячи задач
         * без создания OS threads на каждую.
         */
        int threadCount = 10000;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            int id = i;
            threads[i] = Thread.startVirtualThread(() -> {
                try {

                    /*
                     * Имитация блокирующей операции.
                     *
                     * Виртуальные потоки во время ожидания не занимают
                     * платформенные потоки, что позволяет масштабировать
                     * большое количество одновременных задач.
                     */
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                lock.lock();
                try {
                    System.out.println("Virtual thread ID: %s".formatted(id));
                } finally {
                    lock.unlock();
                }
            });
        }

        for (Thread thread : threads) {
            try {

                /*
                 * Ожидаем завершения всех виртуальных потоков,
                 * прежде чем завершить main.
                 */
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("All virtual threads have finished");
    }
}