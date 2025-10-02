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
    private static final TaskManager taskManager = Managers.getDefault();
    private static final Gson gson = GsonCreator.getGson();

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler(taskManager, gson));
        httpServer.start();
        System.out.println("Server had been started on 8080 port!");
    }
}
