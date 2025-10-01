package server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import server.utils.HttpStatus;
import task.Task;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

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
                System.out.println("Nachalnika ne rugaysya");
        }
    }

    private void getHandle(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2) {
            sendText(httpExchange, HttpStatus.OK.code, gson.toJson(taskManager.getTasks()));
        } else if (pathParts.length == 3) {
            try {
                int taskId = Integer.parseInt(pathParts[2]);
                sendText(httpExchange, HttpStatus.OK.code, gson.toJson(taskManager.getTaskById(taskId)));
            } catch (NumberFormatException e) {
                sendNotFound(httpExchange, String.format("Wrong task ID format: %s (%s)", pathParts[2], e.getMessage()));
            } catch (NullPointerException e) {
                sendBadRequest(httpExchange, String.format("There is no Task with ID = %s", pathParts[2]));
            }
        } else {
            sendNotFound(httpExchange, "There is no such endpoint");
        }
    }

    private void postHandle(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2) {
            try {
                Task task = gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET), Task.class);
                int taskId = task.getId();
                String resposeText;

                if (taskId == 0) {
                    taskId = taskManager.createNewTask(task);
                    resposeText = String.format("Task with ID = %d created", taskId);
                } else {
                    if (task.getStatus() == null) {
                        sendBadRequest(httpExchange, "Invalid request: status field cannot be null");
                        return;
                    } else {
                        taskManager.updateTask(task);
                        resposeText = String.format("Task with ID = %d updated", taskId);
                    }
                }
                sendText(httpExchange, HttpStatus.CREATED.code, resposeText);
            } catch (JsonSyntaxException e) {
                sendBadRequest(httpExchange, String.format("Invalid request body: check JSON structure or field values (%s)", e.getMessage()));
            } catch (NullPointerException e) {
                sendHasOverlaps(httpExchange);
            }
        } else {
            sendNotFound(httpExchange, "There is no such endpoint");
        }
    }

    private void deleteHandle(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 3) {
            try {
                int taskId = Integer.parseInt(pathParts[2]);
                taskManager.deleteTaskById(taskId);
                sendText(httpExchange, HttpStatus.OK.code, String.format("Task with ID = %d was deleted", taskId));
            } catch (NumberFormatException e) {
                sendNotFound(httpExchange, String.format("Wrong task ID format: %s.(%s)", pathParts[2], e.getMessage()));
            } catch (NullPointerException e) {
                sendBadRequest(httpExchange, String.format("There is no Task with ID = %s", pathParts[2]));
            }
        } else {
            sendNotFound(httpExchange, "There is no such endpoint");
        }
    }
}