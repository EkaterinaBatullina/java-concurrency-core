package com.technokratos.counter;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {

    /*
     * Инкремент вида counter++ не является атомарной операцией:
     * он включает чтение значения, увеличение и запись результата.
     *
     * AtomicInteger выполняет обновление через Compare-And-Swap,
     * используя атомарные инструкции процессора, что позволяет
     * нескольким потокам безопасно изменять общий счетчик без synchronized.
     */
    private final AtomicInteger counter = new AtomicInteger(0);

    public void increment() {
        counter.incrementAndGet();
    }

    public int getCounter() {
        return counter.get();
    }
}