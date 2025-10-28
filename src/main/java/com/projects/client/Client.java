package com.projects.client;


import com.projects.common.Request;
import com.projects.common.Response;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Scanner;

import static java.net.HttpURLConnection.*;

public class Client {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private static final Path path = Path.of(System.getProperty("user.dir"), "src", "main", "java", "com", "projects", "client", "data");

    Client(String address, int port) {
        try {
            socket = new Socket(InetAddress.getByName(address), port);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public Response sendRequest(Request request) {
        try {
            dataOutputStream.writeUTF(request.toString());
            return Response.parseResponse(dataInputStream.readUTF());
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public Response sendRequest(Request request, File file) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
            dataOutputStream.writeUTF(request.toString());
            if (request.getType() == Request.RequestType.PUT) {
                dataOutputStream.writeInt((int) file.length());
                bufferedInputStream.transferTo(dataOutputStream);
            }
            return Response.parseResponse(dataInputStream.readUTF());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }


    public void processResponse(Response response) {
        switch (response.getRequestType()) {
            case GET -> {
                switch (response.getStatusCode()) {
                    case HTTP_OK -> saveFile();
                    case HTTP_NOT_FOUND -> System.out.printf("The response says that this file is not found!%n");
                    default -> System.out.println("Invalid response");
                }
            }
            case PUT -> {
                switch (response.getStatusCode()) {
                    case HTTP_OK -> System.out.printf("Response says that file is saved! ID = %s%n", response.getFileID());
                    case HTTP_FORBIDDEN -> System.out.printf("The response says that file is not saved!%n");
                    default -> System.out.println("Invalid response");
                }
            }
            case DELETE -> {
                switch (response.getStatusCode()) {
                    case HTTP_OK -> System.out.printf("The response says that this file was deleted successfully!%n");
                    case HTTP_NOT_FOUND -> System.out.printf("The response says that this file is not found!%n");
                    default -> System.out.println("Invalid response");
                }
            }
            case EXIT -> {
                switch (response.getStatusCode()) {
                    case HTTP_OK -> System.out.printf("The response says that server closed successfully!%n");
                    case HTTP_NOT_FOUND -> System.out.printf("The response says that server issued an error%n");
                    default -> System.out.println("Invalid response");
                }
            }
            default -> System.out.println("Unexpected response for request type " + response.getRequestType());
        }
    }

    private void saveFile() {
        System.out.println("The file was downloaded! Specify a name for it: ");

        try (Scanner scanner = new Scanner(System.in)) {
            String fileName = scanner.nextLine();
            File file = path.resolve(fileName).toFile();
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
                int fileLength = dataInputStream.readInt();
                byte[] buffer = new byte[fileLength];
                dataInputStream.readFully(buffer, 0, fileLength);
                bos.write(buffer, 0, fileLength);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("File saved on the hard drive!");
    }

    public void exit() {
        try {
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.exit(0);
    }
}
