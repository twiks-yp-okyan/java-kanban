package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerHistoryAndPrioritizedTests {
    private final HttpTaskServer httpServer = new HttpTaskServer(Managers.getDefault(), GsonCreator.getGson());
    private final TaskManager taskManager = httpServer.getManager();
    private final Gson gson = httpServer.getGson();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    public void startServer() throws IOException {
        httpServer.start();
        int task1 = taskManager.createNewTask(new Task("Task 1", "Description for task 1", LocalDateTime.now(), Duration.ofMinutes(30)));
        int task2 = taskManager.createNewTask(new Task("Task 2", "Description for task 2", LocalDateTime.now().minusHours(1), Duration.ofMinutes(30)));
        int task3 = taskManager.createNewTask(new Task("Task 3", "Description for task 3", LocalDateTime.now().plusHours(1), Duration.ofMinutes(30)));
        int epicId = taskManager.createNewEpic(new Epic("Epic", "Description"));
        int subtask1 = taskManager.createNewSubtask(new Subtask("Subtask", "Description for subtask 1", LocalDateTime.now().minusDays(1), Duration.ofMinutes(30), epicId));
        int subtask2 = taskManager.createNewSubtask(new Subtask("Subtask 1", "Description for subtask 1", LocalDateTime.now().minusHours(5), Duration.ofMinutes(30), epicId));
        int subtask3 = taskManager.createNewSubtask(new Subtask("Subtask 2", "Description for subtask 2", LocalDateTime.now().plusHours(10), Duration.ofMinutes(30), epicId));

        Task taskForHistory = taskManager.getTaskById(task1);
        taskForHistory = taskManager.getTaskById(task3);
        taskForHistory = taskManager.getSubtaskById(subtask2);
        taskForHistory = taskManager.getEpicById(epicId);
    }

    @AfterEach
    public void stopServer() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
        httpServer.stop();
    }

    @Test
    public void shouldReturnFourViewsInHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.OK.code, response.statusCode());

        JsonArray history = JsonParser.parseString(response.body()).getAsJsonArray();
        assertEquals(4, history.size());
    }

    @Test
    public void shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.OK.code, response.statusCode());

        Optional<Task> firstTask = taskManager.getPrioritizedTasks().stream().findFirst();
        JsonArray prioritizedTasks = JsonParser.parseString(response.body()).getAsJsonArray();
        assertEquals(firstTask.get(), gson.fromJson(prioritizedTasks.get(0), Subtask.class)); // заранее знаю, что будет именно сабтаск
    }
}
