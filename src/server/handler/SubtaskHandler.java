package server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerSaveException;
import exceptions.NotAcceptedTaskException;
import exceptions.NotFoundTaskException;
import manager.TaskManager;
import server.utils.HttpStatus;
import task.Subtask;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    void getHandle(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2) {
            sendText(httpExchange, HttpStatus.OK.code, gson.toJson(taskManager.getSubtasks()));
        } else if (pathParts.length == 3) {
            try {
                int subtaskId = Integer.parseInt(pathParts[2]);
                sendText(httpExchange, HttpStatus.OK.code, gson.toJson(taskManager.getSubtaskById(subtaskId)));
            } catch (NumberFormatException e) {
                sendBadRequest(httpExchange, String.format("Wrong subtask ID format: %s (%s)", pathParts[2], e.getMessage()));
            } catch (NotFoundTaskException e) {
                sendNotFound(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "There is no such endpoint");
        }
    }

    void postHandle(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2) {
            try {
                Subtask subtask = gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET), Subtask.class);
                int subtaskId = subtask.getId();
                String responseText;

                if (subtask.getEpicId() == 0) {
                    sendBadRequest(httpExchange, "Invalid request: epicId field cannot be null");
                    return;
                }

                if (subtaskId == 0) {
                    subtaskId = taskManager.createNewSubtask(subtask);
                    responseText = String.format("Subtask with ID = %d created", subtaskId);
                } else {
                    if (subtask.getStatus() == null) {
                        sendBadRequest(httpExchange, "Invalid request: status field cannot be null");
                        return;
                    } else {
                        taskManager.updateSubtask(subtask);
                        responseText = String.format("Subtask with ID = %d updated", subtaskId);
                    }
                }
                sendText(httpExchange, HttpStatus.CREATED.code, responseText);
            } catch (JsonSyntaxException e) {
                sendBadRequest(httpExchange, String.format("Invalid request body: check JSON structure or field values (%s)", e.getMessage()));
            } catch (NotAcceptedTaskException e) {
                sendHasOverlaps(httpExchange, e.getMessage());
            } catch (ManagerSaveException e) {
                sendInternalError(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "There is no such endpoint");
        }
    }

    void deleteHandle(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2) {
            taskManager.deleteAllSubtasks();
            sendText(httpExchange, HttpStatus.OK.code, "All subtasks had been deleted");
        } else if (pathParts.length == 3) {
            try {
                int subtaskId = Integer.parseInt(pathParts[2]);
                taskManager.deleteSubtaskById(subtaskId);
                sendText(httpExchange, HttpStatus.OK.code, String.format("Subtask with ID = %d was deleted", subtaskId));
            } catch (NumberFormatException e) {
                sendBadRequest(httpExchange, String.format("Wrong Subtask ID format: %s.(%s)", pathParts[2], e.getMessage()));
            } catch (NotFoundTaskException e) {
                sendNotFound(httpExchange, e.getMessage());
            } catch (ManagerSaveException e) {
                sendInternalError(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "There is no such endpoint");
        }
    }
}
