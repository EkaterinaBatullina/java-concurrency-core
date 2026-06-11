package com.technokratos.executorservice;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

public class SortTask implements Callable<int[]> {
    private final int[] originalArray;
    private final ReentrantLock logLock;

    public SortTask(int[] originalArray, ReentrantLock logLock) {
        this.originalArray = originalArray;
        this.logLock = logLock;
    }

    @Override
    public int[] call() {
        int[] sortedArray = Arrays.copyOf(originalArray, originalArray.length);
        Arrays.sort(sortedArray);

        logLock.lock();
        try {
            System.out.println("Sorted array: %s".formatted(Arrays.toString(sortedArray)));
        } finally {
            logLock.unlock();
        }

        return sortedArray;
    }
}