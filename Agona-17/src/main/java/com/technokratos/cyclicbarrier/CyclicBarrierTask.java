package com.technokratos.cyclicbarrier;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrierTask {
    private static final ReentrantLock logLock = new ReentrantLock();

    public static void main(String[] args) {
        int workers = 3;

        /*
         * Барьер срабатывает после прихода всех участников.
         */
        CyclicBarrier barrier = new CyclicBarrier(
                workers,
                () -> System.out.println("All workers reached barrier. Starting next phase...")
        );

        try (ExecutorService executor =
                     Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 1; i <= workers; i++) {
                int workerId = i;

                executor.submit(() -> {
                    try {
                        log("Worker %d: phase 1 started".formatted(workerId));

                        Thread.sleep(workerId * 1000L);

                        log("Worker %d: waiting at barrier".formatted(workerId));

                        /*
                         * Поток блокируется до тех пор,
                         * пока все участники не достигнут барьера.
                         */
                        barrier.await();

                        log("Worker %d: phase 2 started".formatted(workerId));

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (BrokenBarrierException e) {
                        log("Barrier was broken");
                    }
                });
            }
        }
    }

    private static void log(String message) {
        logLock.lock();
        try {
            System.out.println(message);
        } finally {
            logLock.unlock();
        }
    }
}