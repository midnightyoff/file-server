package com.projects.client;


import com.projects.common.Request;
import com.projects.common.Response;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Request.RequestType requestType = null;
        while (requestType == null) {
            System.out.println("Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");
            String action = scanner.nextLine();
            requestType = switch (action) {
                case "1" -> Request.RequestType.GET;
                case "2" -> Request.RequestType.PUT;
                case "3" -> Request.RequestType.DELETE;
                case "exit" -> Request.RequestType.EXIT;
                default -> null;
            };
        }
        Client client = new Client("127.0.0.1", 8080);
        Response response;
        if (requestType == Request.RequestType.EXIT) {
            response = client.sendRequest(new Request(requestType));
        } else {
            System.out.println("Enter filename: ");
            String filename = scanner.nextLine();
            if (requestType == Request.RequestType.GET || requestType == Request.RequestType.DELETE) {
                response = client.sendRequest(new Request(requestType, filename));
            } else {
                System.out.println("Enter file content: ");
                String content = scanner.nextLine();
                response = client.sendRequest(new Request(requestType, filename, content));
            }
        }
        System.out.println("The request was sent.");
        System.out.println(response.getBody());
    }
}