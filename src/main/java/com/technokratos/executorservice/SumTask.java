package com.technokratos.executorservice;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

public class SumTask implements Callable<Integer> {
    private final int[] numbers;
    private final ReentrantLock logLock;

    public SumTask(int[] numbers, ReentrantLock logLock) {
        this.numbers = numbers;
        this.logLock = logLock;
    }

    @Override
    public Integer call() {
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }

        logLock.lock();
        try {
            System.out.println("Sum calc finished: %s".formatted(sum));
        } finally {
            logLock.unlock();
        }
        return sum;
    }
}