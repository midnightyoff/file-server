package com.projects.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 8080;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS));
             Socket socket = serverSocket.accept();
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
            System.out.println("Server started");
            System.out.println("Received: " + dataInputStream.readUTF());
            String message = "All files were sent";
            dataOutputStream.writeUTF(message);
            System.out.println("Sent: " + message);
        } catch (IOException ioException) {
            System.out.println("Error: " + ioException.getMessage());
        }
    }
}
