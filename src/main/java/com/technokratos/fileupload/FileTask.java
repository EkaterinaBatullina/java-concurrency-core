package com.technokratos.fileupload;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class FileTask {
    private static final ReentrantLock logLock = new ReentrantLock();

    public static void main(String[] args) {
        int filesCount = 5;

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 1; i <= filesCount; i++) {
                int fileId = i;

                executor.submit(() -> {
                    for (int progress = 0; progress <= 100; progress += 10) {

                        logLock.lock();
                        try {
                            System.out.println("File %s: %s%%".formatted(fileId, progress));
                        } finally {
                            logLock.unlock();
                        }

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    logLock.lock();
                    try {
                        System.out.println("File %s uploaded!".formatted(fileId));
                    } finally {
                        logLock.unlock();
                    }
                });
            }
        }

        System.out.println("All files processing completed.");
    }
}