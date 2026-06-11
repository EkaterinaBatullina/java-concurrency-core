package com.technokratos.countdownlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchTask {

    public static void main(String[] args) throws InterruptedException {

        /*
         * Счётчик инициализируется количеством задач,
         * завершения которых необходимо дождаться.
         */
        CountDownLatch latch = new CountDownLatch(3);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 1; i <= 3; i++) {
                int workerId = i;

                executor.submit(() -> {
                    try {
                        System.out.println("Worker %d started".formatted(workerId));

                        Thread.sleep(1000L * workerId);

                        System.out.println("Worker %d finished".formatted(workerId));

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {

                        /*
                         * Уменьшаем счётчик независимо от результата
                         * выполнения задачи.
                         */
                        latch.countDown();
                    }
                });
            }

            System.out.println("Main thread waits...");

            /*
             * Блокируемся до тех пор,
             * пока счётчик не станет равен нулю.
             */
            latch.await();

            System.out.println("All workers completed. Continue processing.");
        }
    }
}