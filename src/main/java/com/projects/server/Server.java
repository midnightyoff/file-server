package com.projects.server;

import com.projects.common.Request;
import com.projects.common.Response;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.*;

public class Server {
    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 8080;
    Path path = Path.of(System.getProperty("user.dir"), "src", "main", "java", "com", "projects", "server", "data");

    Server() {
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
                    Response response = switch (request.getType()) {
                        case GET -> getFile(request.getFileName());
                        case DELETE -> deleteFile(request.getFileName());
                        case PUT -> putFile(request.getFileName(), request.getData());
                        case EXIT -> exit();
                    };
                    dataOutputStream.writeUTF(response.toString());
                    if (request.getType() == Request.RequestType.EXIT) System.exit(0);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private Response getFile(String fileName) {
        Response response;
        File file = path.resolve(fileName).toFile();
        if (!file.exists()) {
            response = new Response(HTTP_NOT_FOUND, "The response says that the file was not found!");
        } else {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                String content = fileReader.lines().collect(Collectors.joining());
                response = new Response(HTTP_OK, String.format("The content of the file is: %s", content));
            } catch (IOException e) {
                response = new Response(HTTP_INTERNAL_ERROR, "The response says that there was an internal error.");
            }
        }
        return response;
    }

    private Response deleteFile(String fileName) {
        Response response;
        File file = path.resolve(fileName).toFile();
        if (!file.exists()) {
            response = new Response(HTTP_NOT_FOUND, "The response says that the file was not found!");
        } else {
            boolean isDeleted = file.delete();
            if (isDeleted) {
                response = new Response(HTTP_OK, "The response says that the file was successfully deleted!");
            } else {
                response = new Response(HTTP_INTERNAL_ERROR, "The response says that there was an internal error.");
            }
        }
        return response;
    }

    private Response putFile(String fileName, String data) {
        File file = path.resolve(fileName).toFile();
        Response response;
        if (file.exists()) {
            response = new Response(HTTP_FORBIDDEN, "The response says that creating the file was forbidden!");
        } else {
            try (FileWriter out = new FileWriter(file)) {
                out.write(data);
                response = new Response(HTTP_OK, "The response says that the file was created!");
            } catch (IOException e) {
                System.err.println("IO error: " + e.getMessage());
                response = new Response(HTTP_INTERNAL_ERROR, "The response says that there was an internal error.");
            }
        }
        return response;
    }

    private Response exit() {
        Response response;
        response = new Response(HTTP_OK, "The response says that the server has exited!");
        return response;
    }

}
