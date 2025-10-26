package com.projects.common;

public class Request {
    public enum RequestType {GET, PUT, DELETE, EXIT}

    private RequestType type;
    private String fileName;
    private String data;

    public Request(RequestType type, String fileName, String data) {
        this.type = type;
        this.fileName = fileName;
        this.data = data;
    }

    public Request(RequestType type, String fileName) {
        this.type = type;
        this.fileName = fileName;
    }

    public Request(RequestType type) {
        this.type = type;
    }

    public RequestType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }

    public static Request parseRequest(String request) {
        String[] parts = request.split(" ", 3);
        try {
            RequestType requestType = RequestType.valueOf(parts[0].toUpperCase());
            String name = parts.length > 1 ? parts[1] : "";
            String data = parts.length > 2 ? parts[2] : "";
            return new Request(requestType, name, data);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid request: " + request);
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", type.name(), fileName, data);
    }
}
