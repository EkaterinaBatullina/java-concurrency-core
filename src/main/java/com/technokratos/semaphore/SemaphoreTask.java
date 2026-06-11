package com.technokratos.semaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class SemaphoreTask {

    /*
     * Semaphore поддерживает внутренний счётчик доступных ресурсов.
     *
     * acquire() уменьшает счетчик, release() увеличивает
     * Нулевой счетчик заставляет поток ожидать освобождения места.
     *
     * Режим fairness=true старается выдавать доступ
     * ожидающим потокам в порядке FIFO.
     */
    private static final Semaphore parkingSpots = new Semaphore(3, true);
    private static final ReentrantLock logLock = new ReentrantLock();

    public static void main(String[] args) {
        for (int i = 1; i <= 6; i++) {
            final String carName = "Car-%s".formatted(i);

            Thread.ofVirtual().start(() -> {
                try {
                    logLock.lock();
                    try {
                        System.out.println("%s is trying to park".formatted(carName));
                    } finally {
                        logLock.unlock();
                    }

                    parkingSpots.acquire();

                    try {
                        logLock.lock();
                        try {
                            System.out.println("%s has parked".formatted(carName));
                            System.out.println("%s is leaving the parking".formatted(carName));
                        } finally {
                            logLock.unlock();
                        }
                    } finally {

                        /*
                         * После завершения работы возвращаем разрешение,
                         * иначе другие потоки могут навсегда потерять доступ к ресурсу.
                         */
                        parkingSpots.release();
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}