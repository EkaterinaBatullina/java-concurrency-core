# Java Concurrency Core - набор примеров многопоточности

## Общая идея

Репозиторий содержит набор изолированных кейсов, демонстрирующих основные модели конкурентности в Java: синхронизацию, lock-free операции, координацию потоков, планирование задач и виртуальные потоки. Каждый пример иллюстрирует отдельный аспект поведения конкурентных систем и их взаимодействие под нагрузкой.

---

## 1. BlockingQueue (producer–consumer)
- Реализован bounded buffer через ArrayBlockingQueue
- Демонстрация backpressure: producer блокируется при заполнении очереди, consumer - при пустой
- Координация потоков через put()/take() без явных wait/notify
- Используется виртуальный поток на каждую задачу (newVirtualThreadPerTaskExecutor)
- Сериализация вывода через ReentrantLock для предотвращения перемешивания логов при конкурентной записи в System.out

---

## 2. AtomicInteger (CAS)
- Показано, что counter++ не является атомарной операцией (read-modify-write)
- Используется AtomicInteger как lock-free механизм синхронизации
- Обновления выполняются через CAS (Compare-And-Swap)
- Гарантируется корректный результат при конкурентных инкрементах без synchronized и Lock

---

## 3. ExecutorService + Callable + Future
- Используется Callable для задач, возвращающих результат
- Асинхронное выполнение через ExecutorService с виртуальными потоками
- Future представляет результат выполнения задачи и её состояние
- get() - блокирующий вызов, ожидающий завершения вычислений
- Поддержан таймаут ожидания через get(timeout, TimeUnit)
- Обработаны исключения выполнения (ExecutionException, TimeoutException, InterruptedException)

---

## 4. Virtual threads (базовая модель)
- Используется newVirtualThreadPerTaskExecutor для запуска задач в виртуальных потоках
- Массовый параллелизм без создания OS threads (one task = one virtual thread)
- Демонстрация блокирующего поведения (sleep) без затрат на platform thread parking
- Имитация I/O-задачи с прогрессом выполнения (upload simulation)
- Сериализация вывода через ReentrantLock для читаемого логирования при высокой конкуренции

---

## 5. HTTP Server (virtual thread per request)
- Каждый HTTP-запрос обрабатывается в отдельном виртуальном потоке (Thread.startVirtualThread)
- Модель request-per-thread без затрат на platform thread на каждый запрос
- Масштабируемая обработка большого числа concurrent HTTP соединений
- Корректное освобождение ресурсов через exchange.close() и try-with-resources для response body
- Сериализация логов через ReentrantLock для читаемого вывода при параллельной обработке запросов
---

## 6. Account + ReentrantLock (synchronization and deadlock avoidance)
- Модель банковского аккаунта с потокобезопасным доступом через ReentrantLock
- Защита критической секции операций deposit, withdraw, getBalance
- Реализован transfer между аккаунтами с потенциальным deadlock сценарием
- Избежание deadlock через tryLock(timeout) и повторные попытки захвата обоих locks
- Используется backoff (Thread.sleep) для предотвращения активного ожидания
- Гарантируется консистентность баланса при конкурентных переводах

---

## 7. ScheduledExecutorService (task scheduling)
- Используется ScheduledExecutorService для периодического выполнения задач
- Модель scheduleWithFixedDelay: следующий запуск происходит после завершения предыдущего
- Демонстрация отличия delay-based scheduling
- Обработка исключений внутри задачи для предотвращения остановки периодического execution
- Показано влияние необработанных ошибок на жизненный цикл scheduled tasks
- Корректное завершение через shutdown() с отложенной остановкой

---

## 8. Semaphore (resource limiting)
- Используется Semaphore для ограничения количества одновременно выполняемых операций
- Модель ограниченного ресурса (parking spots = 3)
- acquire()/release() управляют доступом к критической секции
- Демонстрация blocking поведения при отсутствии доступных permits
- Используется fairness mode (fair = true) для FIFO распределения доступа
- Гарантированное освобождение ресурса через finally блок

---

## 9. VirtualThreadTask (scalability demonstration)
- Запуск большого количества виртуальных потоков (10_000 concurrent tasks)
- Демонстрация масштабируемости виртуальных потоков по сравнению с платформенными
- Блокирующая операция (Thread.sleep) не приводит к блокировке OS thread
- Показано поведение виртуальных потоков при массовом параллельном запуске
- Синхронизация вывода через ReentrantLock для читаемого логирования результатов

---

## Итог

Репозиторий демонстрирует базовые и прикладные механизмы конкурентности в Java через набор изолированных кейсов:

- модели синхронизации (locks, Semaphore)
- lock-free подход (CAS через AtomicInteger)
- координация потоков (BlockingQueue, ExecutorService, Future)
- планирование задач (ScheduledExecutorService)
- обработка I/O и request-per-thread модель
- виртуальные потоки и их масштабирование

Проекты иллюстрируют ключевые свойства конкурентных систем:
blocking behavior, race conditions, deadlock avoidance, task scheduling semantics и scalability при высокой конкуренции.
