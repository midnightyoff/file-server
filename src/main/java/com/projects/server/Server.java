package com.projects.server;

import com.projects.common.Request;
import com.projects.common.Response;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.HashMap;

import static java.net.HttpURLConnection.*;

public class Server {
    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 8080;
    Path path = Path.of(System.getProperty("user.dir"), "src", "main", "java", "com", "projects", "server", "data");
    private HashMap<Integer, String> files = new HashMap<>();
    private int fileID = 0;

    Server() {
        loadFilesMap();
        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))) {
            System.out.println("Server started");
            while (true) {
                try (Socket socket = serverSocket.accept();
                     DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                     DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
                    Request request = Request.parseRequest(dataInputStream.readUTF());
                    switch (request.getType()) {
                        case GET -> getFile(request.getBy(), request.getFileName(), dataOutputStream);
                        case DELETE -> deleteFile(request.getBy(), request.getFileName(), dataOutputStream);
                        case PUT -> putFile(request.getFileName(), dataOutputStream, dataInputStream);
                        case EXIT -> exit();
                    }
                    if (request.getType() == Request.RequestType.EXIT) {
                        System.exit(0);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            System.out.println("Server stopped and saved");
            saveFilesMap();
        }
    }

    private void getFile(Request.ProcessFileBy processFileBy, String fileName, DataOutputStream dataOutputStream) throws IOException {
        Response response;
        try {
            if (processFileBy == Request.ProcessFileBy.BY_ID) {
                fileName = files.get(Integer.parseInt(fileName));
            }
            File file = path.resolve(fileName).toFile();
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
                response = new Response(Request.RequestType.GET, HTTP_OK);
                dataOutputStream.writeUTF(response.toString());
                int size = (int) file.length();
                dataOutputStream.writeInt(size);
                bufferedInputStream.transferTo(dataOutputStream);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response = new Response(Request.RequestType.GET, HTTP_NOT_FOUND);
            dataOutputStream.writeUTF(response.toString());
        }
    }

    private void deleteFile(Request.ProcessFileBy processFileBy, String fileName, DataOutputStream dataOutputStream) throws IOException {
        Response response;
        int fileId = Integer.parseInt(fileName);
        try {
            if (processFileBy == Request.ProcessFileBy.BY_ID) {
                fileName = files.get(fileId);
            }
            File file = path.resolve(fileName).toFile();
            if (!file.exists()) {
                response = new Response(Request.RequestType.DELETE, HTTP_NOT_FOUND);
            } else {
                boolean isDeleted = file.delete();
                if (isDeleted) {
                    files.remove(fileId);
                    response = new Response(Request.RequestType.DELETE, HTTP_OK);
                } else {
                    response = new Response(Request.RequestType.DELETE, HTTP_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response = new Response(Request.RequestType.DELETE, HTTP_INTERNAL_ERROR);
        }
        dataOutputStream.writeUTF(response.toString());
    }

    private void putFile(String fileName, DataOutputStream dataOutputStream,DataInputStream dataInputStream) throws IOException {
        File file = path.resolve(fileName).toFile();
        Response response;
        if (file.exists()) {
            response = new Response(Request.RequestType.PUT, HTTP_FORBIDDEN);
        } else {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                int size = dataInputStream.readInt();
                byte[] bytes = new byte[size];
                dataInputStream.readFully(bytes, 0, size);
                bufferedOutputStream.write(bytes, 0, size);
                files.put(fileID++, fileName);
                response = new Response(Request.RequestType.PUT, HTTP_OK);
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                response = new Response(Request.RequestType.PUT, HTTP_NOT_FOUND);
            }
        }
        dataOutputStream.writeUTF(response.toString());
    }

    private void saveFilesMap() {
        File file = path.resolve("filesMap").toFile();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            objectOutputStream.writeObject(files);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void loadFilesMap() {
        File file = path.resolve("filesMap").toFile();
        if (file.exists()) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
                files = (HashMap<Integer, String>) objectInputStream.readObject();
                fileID = files.size();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private Response exit() {
        Response response;
        response = new Response(Request.RequestType.EXIT, HTTP_OK);
        return response;
    }

}
