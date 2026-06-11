package com.technokratos.counter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CounterTask {

    public static void main(String[] args) {
        Counter counter = new Counter();

        /*
         * Обе задачи работают с одним экземпляром Counter.
         *
         * Благодаря AtomicInteger инкременты выполняются атомарно,
         * поэтому итоговое значение будет корректным даже при
         * одновременном доступе из нескольких потоков.
         */
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        };

        /*
         * После выхода из try-with-resources executor завершает работу
         * и дожидается окончания всех ранее отправленных задач.
         *
         * Поэтому к моменту вывода результата оба потока уже выполнят
         * свои 1000 инкрементов, и ожидаемое значение счетчика составит 2000.
         */
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(task);
            executor.submit(task);
        }

        System.out.println("Final counter value: %s".formatted(counter.getCounter()));
    }
}