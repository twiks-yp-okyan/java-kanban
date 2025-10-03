package server;

import com.google.gson.Gson;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.utils.GsonCreator;
import server.utils.HttpStatus;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpTaskManagerSubtasksTest {
    private final HttpTaskServer httpServer = new HttpTaskServer(Managers.getDefault(), GsonCreator.getGson());
    private final TaskManager taskManager = httpServer.getManager();
    private final Gson gson = httpServer.getGson();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    public void startServer() throws IOException {
        httpServer.start();
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();

        taskManager.createNewEpic(new Epic("Epic", "Description")); // id = 1 always
    }

    @AfterEach
    public void stopServer() {
        httpServer.stop();
    }

    @Test
    public void shouldCreateSubtaskAndAddToEpicSubtasksIds() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask 1", "Description for subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), 1);
        String subtaskString = gson.toJson(subtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskString))
                .uri(url)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.CREATED.code, response.statusCode());
        assertEquals(taskManager.getSubtaskById(taskManager.getEpicById(1).getSubtasksIds().getFirst()).getName(), subtask.getName());
    }

    @Test
    public void shouldDeleteSubtaskAndRemoveIdFromEpicSubtasksIds() throws IOException, InterruptedException {
        int subtaskId = taskManager.createNewSubtask(new Subtask("Subtask 1", "Description for subttask 1", LocalDateTime.now(), Duration.ofMinutes(30), 1));

        URI url = URI.create(String.format("http://localhost:8080/subtasks/%d", subtaskId));
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.OK.code, response.statusCode());
        assertEquals(0, taskManager.getEpicById(1).getSubtasksIds().size());
        assertThrows(NullPointerException.class, () -> taskManager.getSubtaskById(subtaskId));
    }

    @Test
    public void shouldNotCreateSubtaskBecauseOfOverlappingWithTask() throws IOException, InterruptedException {
        taskManager.createNewTask(new Task("Task 1", "Description for task 1", LocalDateTime.now(), Duration.ofMinutes(30)));

        Subtask subtask = new Subtask("Subtask 1", "Description for subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), 1);
        String subtaskString = gson.toJson(subtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskString))
                .uri(url)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.NOT_ACCEPTABLE.code, response.statusCode());
    }
}
