package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import server.handler.*;
import server.utils.GsonCreator;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager taskManager;
    private final Gson gson;
    private static HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault(), GsonCreator.getGson());
        httpTaskServer.start();
        System.out.println("Server had been started on 8080 port!");
    }

    public void start() throws IOException {
        createServer();
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }

    public TaskManager getManager() {
        return taskManager;
    }

    public Gson getGson() {
        return gson;
    }

    private void createServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler(taskManager, gson));
    }
}
