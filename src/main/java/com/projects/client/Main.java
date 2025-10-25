package com.projects.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Main {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
            System.out.println("Client started");
            String message = "Give me everything you have!";
            dataOutputStream.writeUTF(message);
            System.out.println("Sent: " + message);
            System.out.println("Received: " + dataInputStream.readUTF());
        } catch (IOException ioException) {
            System.out.println("Error: " + ioException.getMessage());
        }
    }
}