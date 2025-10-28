package com.projects.server;

import com.projects.common.Request;
import com.projects.common.Response;

import java.io.*;
import java.net.Socket;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

public class Session implements Runnable {

    private final Socket socket;
    private final Storage storage;
    private final Server server;

    Session(Socket socket, Storage storage, Server server) {
        this.socket = socket;
        this.storage = storage;
        this.server = server;
    }

    @Override
    public void run() {
        try (Socket socket = this.socket;
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
            Request request = Request.parseRequest(dataInputStream.readUTF());
            switch (request.getType()) {
                case GET -> getFile(request.getBy(), request.getFileName(), dataOutputStream);
                case DELETE -> deleteFile(request.getBy(), request.getFileName(), dataOutputStream);
                case PUT -> putFile(request.getFileName(), dataOutputStream, dataInputStream);
                case EXIT -> exit(dataOutputStream);
            }
            if (request.getType() == Request.RequestType.EXIT) {
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void getFile(Request.ProcessFileBy processFileBy, String fileName, DataOutputStream dataOutputStream) throws IOException {
        Response response;
        File file = Storage.getInstance().getFile(processFileBy, fileName);
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
            response = new Response(Request.RequestType.GET, HTTP_OK);
            dataOutputStream.writeUTF(response.toString());
            int size = (int) file.length();
            dataOutputStream.writeInt(size);
            bufferedInputStream.transferTo(dataOutputStream);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response = new Response(Request.RequestType.GET, HTTP_NOT_FOUND);
            dataOutputStream.writeUTF(response.toString());
        }
    }

    private void deleteFile(Request.ProcessFileBy processFileBy, String fileName, DataOutputStream dataOutputStream) throws IOException {
        Response response;
        File file = Storage.getInstance().getFile(processFileBy, fileName);
        try {
            if (storage.deleteFile(file)) {
                response = new Response(Request.RequestType.DELETE, HTTP_OK);
            } else {
                response = new Response(Request.RequestType.DELETE, HTTP_NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response = new Response(Request.RequestType.DELETE, HTTP_NOT_FOUND);
        }
        dataOutputStream.writeUTF(response.toString());
    }

    private void putFile(String fileName, DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {
        Response response;
        File file = Storage.getInstance().getFile(fileName);
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            int size = dataInputStream.readInt();
            byte[] bytes = new byte[size];
            dataInputStream.readFully(bytes, 0, size);
            bufferedOutputStream.write(bytes, 0, size);
            Storage.getInstance().putFile(fileName);
            response = new Response(Request.RequestType.PUT, HTTP_OK);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response = new Response(Request.RequestType.PUT, HTTP_NOT_FOUND);
        }
        dataOutputStream.writeUTF(response.toString());
    }


    private void exit(DataOutputStream dataOutputStream) throws IOException {
        Response response = new Response(Request.RequestType.EXIT, HTTP_OK);
        dataOutputStream.writeUTF(response.toString());
        server.shutdown();
    }
}
