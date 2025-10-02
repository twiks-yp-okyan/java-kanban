package server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerSaveException;
import manager.TaskManager;
import server.utils.HttpStatus;
import task.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    void getHandle(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2) {
            sendText(httpExchange, HttpStatus.OK.code, gson.toJson(taskManager.getEpics()));
        } else if (pathParts.length == 3) {
            try {
                int epicId = Integer.parseInt(pathParts[2]);
                sendText(httpExchange, HttpStatus.OK.code, gson.toJson(taskManager.getEpicById(epicId)));
            } catch (NumberFormatException e) {
                sendNotFound(httpExchange, String.format("Wrong epic ID format: %s (%s)", pathParts[2], e.getMessage()));
            } catch (NullPointerException e) {
                sendBadRequest(httpExchange, String.format("There is no Epic with ID = %s", pathParts[2]));
            }
        } else {
            sendNotFound(httpExchange, "There is no such endpoint");
        }
    }

    void postHandle(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2) {
            try {
                Epic epic = gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET), Epic.class);
                int epicId = epic.getId();
                String responseText;

                if (epic.getStartTime() != null || epic.getDuration() != null) {
                    sendBadRequest(httpExchange, "Epic does not have it's own startTime & duration");
                    return;
                } else if (epic.getSubtasksIds() == null) { // не знаю, как обработать ситуацию, когда этого поля просто нет в запросе, но при этом оно десериализуется в пустой список
                    sendBadRequest(httpExchange, "Field subtasksIds should exits in request body");
                    return;
                } else if (epicId == 0) {
                    epicId = taskManager.createNewEpic(epic);
                    responseText = String.format("Epic with ID = %d created", epicId);
                } else {
                    taskManager.updateEpic(epic);
                    responseText = String.format("Epic with ID = %d updated", epicId);
                }
                sendText(httpExchange, HttpStatus.CREATED.code, responseText);
            } catch (JsonSyntaxException e) {
                sendBadRequest(httpExchange, "Invalid request body: check JSON structure or field values");
            } catch (NullPointerException e) {
                sendHasOverlaps(httpExchange);
            } catch (ManagerSaveException e) {
                sendInternalError(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "There is no such endpoint");
        }
    }

    void deleteHandle(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 3) {
            try {
                int epicId = Integer.parseInt(pathParts[2]);
                taskManager.deleteEpicById(epicId);
                sendText(httpExchange, HttpStatus.OK.code, String.format("Epic with ID = %d was deleted", epicId));
            } catch (NumberFormatException e) {
                sendNotFound(httpExchange, String.format("Wrong epic ID format: %s.(%s)", pathParts[2], e.getMessage()));
            } catch (NullPointerException e) {
                sendBadRequest(httpExchange, String.format("There is no Epic with ID = %s", pathParts[2]));
            } catch (ManagerSaveException e) {
                sendInternalError(httpExchange, e.getMessage());
            }
        } else {
            sendNotFound(httpExchange, "There is no such endpoint");
        }
    }
}
