package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import server.utils.HttpStatus;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    void getHandle(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2) {
            sendText(httpExchange, HttpStatus.OK.code, gson.toJson(taskManager.getHistory()));
        } else {
            sendNotFound(httpExchange, "There is no such endpoint");
        }
    }

    void postHandle(HttpExchange httpExchange) throws IOException {
        sendNotFound(httpExchange, "There is no such endpoint");
    }

    void deleteHandle(HttpExchange httpExchange) throws IOException {
        sendNotFound(httpExchange, "There is no such endpoint");
    }
}
