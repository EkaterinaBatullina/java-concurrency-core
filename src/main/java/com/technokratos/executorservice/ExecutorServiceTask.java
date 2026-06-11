package com.technokratos.executorservice;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class ExecutorServiceTask {
    private static final ReentrantLock logLock = new ReentrantLock();

    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            int[] sortArray = {5, 3, 8, 1};
            int[] sumArray = {10, 20, 30, 40};

            /*
             * В отличие от Runnable, Callable может возвращать
             * результат и выбрасывать проверяемые исключения.
             */
            Callable<int[]> sortTask = new SortTask(sortArray, logLock);
            Callable<Integer> sumTask = new SumTask(sumArray, logLock);

            /*
             * Future хранит результат задачи, выполняющейся в другом потоке.
             *
             * Несмотря на асинхронное выполнение самой задачи,
             * вызов get() является блокирующей операцией и заставляет
             * текущий поток ждать завершения вычислений.
             */
            Future<int[]> sortFuture = executor.submit(sortTask);
            Future<Integer> sumFuture = executor.submit(sumTask);

            /*
             * Время ожидания результата ограничено.
             *
             * Если задача не завершится за 2 секунды,
             * будет выброшен TimeoutException.
             */
            int[] sortedResult = sortFuture.get(2, TimeUnit.SECONDS);
            int sumResult = sumFuture.get(2, TimeUnit.SECONDS);

            logLock.lock();
            try {
                System.out.println("Sorted result: %s".formatted(Arrays.toString(sortedResult)));
                System.out.println("Sum result: %s".formatted(sumResult));
            } finally {
                logLock.unlock();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Task execution failed with error: " + e.getCause().getMessage());
        } catch (TimeoutException e) {
            System.err.println("ALERT: Future execution timeout exceeded!");
        }
    }
}