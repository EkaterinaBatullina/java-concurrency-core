package com.technokratos.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReentrantLock;

public class ServerTask {
    private static final ReentrantLock logLock = new ReentrantLock();

    public static void main(String[] args) {
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start server network stack", e);
        }

        /*
         * Каждый входящий HTTP-запрос обрабатывается в отдельном
         * виртуальном потоке.
         *
         * Такой подход позволяет масштабировать большое количество
         * одновременно ожидающих запросов без создания отдельного
         * платформенного потока для каждого клиента.
         */
        server.createContext("/", exchange ->
                Thread.startVirtualThread(() -> handleRequest(exchange))
        );

        server.start();
        System.out.println("HTTP Server successfully started on port 8080...");
    }

    private static void handleRequest(HttpExchange exchange) {
        try {
            logLock.lock();
            try {
                System.out.println("Handling request in virtual thread: %s".formatted(Thread.currentThread()));
            } finally {
                logLock.unlock();
            }

            String response = "Hello from virtual thread!";
            byte[] responseBytes = response.getBytes();

            exchange.sendResponseHeaders(200, responseBytes.length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(responseBytes);
                outputStream.flush();
            }

        } catch (IOException e) {
            logLock.lock();
            try {
                System.err.println("Network I/O error during request handling: " + e.getMessage());
            } finally {
                logLock.unlock();
            }
        } finally {

            /*
             * Освобождаем ресурсы HTTP-обмена независимо от результата
             * обработки запроса.
             */
            exchange.close();
        }
    }
}