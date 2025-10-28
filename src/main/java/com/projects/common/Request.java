package com.projects.common;

public class Request {
    public enum RequestType {GET, PUT, DELETE, EXIT}
    public enum ProcessFileBy {BY_ID, BY_NAME}

    private final RequestType type;
    private ProcessFileBy by;
    private String fileName;

    public Request(RequestType type, ProcessFileBy by, String fileName) {
        this.type = type;
        this.by = by;
        this.fileName = fileName;
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

    public String getFileName() {
        return fileName;
    }

    public ProcessFileBy getBy() {
        return by;
    }

    //GET BY_NAME name
    //DELETE BY_ID id
    //PUT NAME
    public static Request parseRequest(String request) {
        String[] parts = request.split(" ", 3);
        try {
            RequestType requestType = RequestType.valueOf(parts[0].toUpperCase());
            if (parts.length == 2) {
                return new Request(requestType, parts[1]);
            } else {
                return new Request(requestType, ProcessFileBy.valueOf(parts[1]), parts[2]);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid request: " + request);
        }
    }

    @Override
    public String toString() {
        if (this.by == null) return String.format("%s %s", type, fileName);
        else return String.format("%s %s %s", type.name(), by.name(), fileName);
    }
}
