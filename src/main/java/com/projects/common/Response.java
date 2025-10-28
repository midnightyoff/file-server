package com.projects.common;


public class Response {
    private final Request.RequestType requestType;
    private final int statusCode;
    private int fileID;

    public Response(Request.RequestType requestType, int statusCode) {
        this.requestType = requestType;
        this.statusCode = statusCode;
    }

    public Response(Request.RequestType requestType, int statusCode, int fileID) {
        this.requestType = requestType;
        this.statusCode = statusCode;
        this.fileID = fileID;
    }

    public static Response parseResponse(String response) {
        String[] parts = response.split(" ", 3);
        try {
            if (parts.length == 3) {
                Request.RequestType requestType = Request.RequestType.valueOf(parts[0]);
                int statusCode = Integer.parseInt(parts[1]);
                int fileID = Integer.parseInt(parts[2]);
                return new Response(requestType, statusCode, fileID);
            } else {
                Request.RequestType requestType = Request.RequestType.valueOf(parts[0]);
                int statusCode = Integer.parseInt(parts[0]);
                return new Response(requestType, statusCode);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public Request.RequestType getRequestType() {
        return requestType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getFileID() {
        return fileID;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", requestType, statusCode, fileID);
    }
}
