package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.utils.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    abstract void getHandle(HttpExchange httpExchange) throws IOException;

    abstract void postHandle(HttpExchange httpExchange) throws IOException;

    abstract void deleteHandle(HttpExchange httpExchange) throws IOException;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        switch (method) {
            case "GET":
                getHandle(httpExchange);
                break;
            case "POST":
                postHandle(httpExchange);
                break;
            case "DELETE":
                deleteHandle(httpExchange);
                break;
            default:
                sendMethodNotAllowed(httpExchange, "Method not allowed");
        }
    }

    void sendText(HttpExchange httpExchange, int statusCode, String text) throws IOException {
        httpExchange.sendResponseHeaders(statusCode, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    void sendNotFound(HttpExchange httpExchange, String text) throws IOException {
        httpExchange.sendResponseHeaders(HttpStatus.NOT_FOUND.code, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    void sendHasOverlaps(HttpExchange httpExchange, String text) throws IOException {
        httpExchange.sendResponseHeaders(HttpStatus.NOT_ACCEPTABLE.code, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    void sendBadRequest(HttpExchange httpExchange, String text) throws IOException {
        httpExchange.sendResponseHeaders(HttpStatus.BAD_REQUEST.code, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    void sendInternalError(HttpExchange httpExchange, String text) throws IOException {
        httpExchange.sendResponseHeaders(HttpStatus.INTERNAL_ERROR.code, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    void sendMethodNotAllowed(HttpExchange httpExchange, String text) throws IOException {
        httpExchange.sendResponseHeaders(HttpStatus.NOT_ALLOWED.code, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }
}
