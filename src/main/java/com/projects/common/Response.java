package com.projects.common;

public class Response {
    private int statusCode;
    private String body;

    public Response(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public Response(String response) {
        String[] split = response.split(" ", 2);
        this.statusCode = Integer.parseInt(split[0]);
        this.body = split[1];
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return String.format("%s %s", statusCode, body);
        }
}
