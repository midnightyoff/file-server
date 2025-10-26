package com.projects.client;


import com.projects.common.Request;
import com.projects.common.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

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
            return new Response(dataInputStream.readUTF());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
}
