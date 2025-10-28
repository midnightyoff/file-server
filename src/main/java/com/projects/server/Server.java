package com.projects.server;


import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class Server {
    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 8080;

    private ServerSocket serverSocket;
    private final AtomicBoolean running = new AtomicBoolean(true);

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public void start() {
        System.out.println("Server started");
        try {
            serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS));
            while (running.get()) {
                try {
                    Socket socket = serverSocket.accept();
                    threadPool.submit(new Session(socket, Storage.getInstance(), this));
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    System.out.println("Server stopped and saved");
                    Storage.getInstance().saveFilesMap();
                    threadPool.shutdown();
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
