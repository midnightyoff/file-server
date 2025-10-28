package com.projects.client;

import com.projects.common.Request;
import com.projects.common.Response;

import java.io.File;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    private final static Scanner scanner = new Scanner(System.in);
    private static final Path path = Path.of(System.getProperty("user.dir"), "src", "main", "java", "com", "projects", "client", "data");

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 8080);
        System.out.println("Enter action (1 - get a file, 2 - save a file, 3 - delete a file): ");
        String action = scanner.nextLine();
        Response response = switch (action) {
            case "1" -> getFileRequest(client);
            case "2" -> saveFileRequest(client);
            case "3" -> deleteFileRequest(client);
            case "exit" -> exitRequest(client);
            default -> throw new IllegalStateException("Unexpected value: " + action);
        };
        if (response != null) {
            System.out.println(response);
            client.processResponse(response);
        }
        client.exit();
    }

    private static Response getFileRequest(Client client) {
        System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id): ");
        String action = scanner.nextLine();
        if (action.equals("1")) {
            System.out.println("Enter the file name: ");
            String fileName = scanner.nextLine();
            Request request = new Request(Request.RequestType.GET, Request.ProcessFileBy.BY_NAME, fileName);
            System.out.println("The request was sent.");
            return client.sendRequest(request);
        } else if (action.equals("2")) {
            System.out.println("Enter id: ");
            String id = scanner.nextLine();
            Request request = new Request(Request.RequestType.GET, Request.ProcessFileBy.BY_ID, id);
            System.out.println("The request was sent.");
            return client.sendRequest(request);
        } else {
            System.out.println("Invalid action");
            return null;
        }
    }

    private static Response saveFileRequest(Client client) {
        System.out.println("Enter name of the file: ");
        String fileName = scanner.nextLine();
        File file = path.resolve(fileName).toFile();
        if (!file.exists() || !file.isFile()) {
            System.out.println("File does not exist: " + fileName);
        } else {
            System.out.println("Enter name of the file to be saved on server: ");
            String serverFileName = scanner.nextLine();
            serverFileName = serverFileName.isEmpty() ? fileName : serverFileName;
            Request request = new Request(Request.RequestType.PUT, serverFileName);
            System.out.println("The request was sent.");
            return client.sendRequest(request, file);
        }
        return null;
    }

    private static Response deleteFileRequest(Client client) {
        System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id): ");
        String action = scanner.nextLine();
        if (action.equals("1")) {
            System.out.println("Enter the file name: ");
            String fileName = scanner.nextLine();
            Request request = new Request(Request.RequestType.DELETE, Request.ProcessFileBy.BY_NAME, fileName);
            System.out.println("The request was sent.");
            return client.sendRequest(request);
        } else if (action.equals("2")) {
            System.out.println("Enter id: ");
            String id = scanner.nextLine();
            Request request = new Request(Request.RequestType.DELETE, Request.ProcessFileBy.BY_ID, id);
            System.out.println("The request was sent.");
            return client.sendRequest(request);
        } else {
            System.out.println("Invalid action");
            return null;
        }
    }

    private static Response exitRequest(Client client) {
        return client.sendRequest(new Request(Request.RequestType.EXIT));
    }
}