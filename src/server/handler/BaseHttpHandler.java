package server.handler;

import com.sun.net.httpserver.HttpExchange;
import server.utils.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public void sendText(HttpExchange httpExchange, int statusCode, String text) throws IOException {
        httpExchange.sendResponseHeaders(statusCode, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    public void sendNotFound(HttpExchange httpExchange, String text) throws IOException {
        httpExchange.sendResponseHeaders(HttpStatus.NOT_FOUND.code, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    public void sendHasOverlaps(HttpExchange httpExchange) throws IOException {
        String responseText = "Task overlaps with existed tasks or does not exist for update";
        httpExchange.sendResponseHeaders(HttpStatus.NOT_ACCEPTABLE.code, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(responseText.getBytes());
        }
    }

    public void sendBadRequest(HttpExchange httpExchange, String text) throws IOException {
        httpExchange.sendResponseHeaders(HttpStatus.BAD_REQUEST.code, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }
}
