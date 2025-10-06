package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import exceptions.NotFoundTaskException;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.utils.GsonCreator;
import server.utils.HttpStatus;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
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
    }

    @AfterEach
    public void stopServer() {
        httpServer.stop();
    }

    @Test
    public void shouldSaveTaskInManager() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description for task 1", LocalDateTime.now(), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.CREATED.code, response.statusCode());
        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    public void shouldReturnListOfTasks() throws IOException, InterruptedException {
        int taskId = taskManager.createNewTask(new Task("Task 1", "Description for task 1", LocalDateTime.now(), Duration.ofMinutes(30)));
        int task2Id = taskManager.createNewTask(new Task("Task 2", "Description for task 2", LocalDateTime.now().minusHours(1), Duration.ofMinutes(30)));

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray tasks = JsonParser.parseString(response.body()).getAsJsonArray();

        Task firstTaskFromManager = gson.fromJson(tasks.get(0), Task.class);
        Task secondTaskFromManager = gson.fromJson(tasks.get(1), Task.class);

        assertEquals(taskManager.getTaskById(taskId), firstTaskFromManager);
        assertEquals(taskManager.getTaskById(task2Id), secondTaskFromManager);
    }

    @Test
    public void shouldUpdateTask() throws IOException, InterruptedException {
        int taskId = taskManager.createNewTask(new Task("Task 1", "Description for task 1", LocalDateTime.now(), Duration.ofMinutes(30)));
        Task updatedTask = new Task(taskId, "Task 1", "Updated Description for task 1", LocalDateTime.now(), Duration.ofMinutes(60), TaskStatus.IN_PROGRESS);
        String updatedTaskJson = gson.toJson(updatedTask);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.CREATED.code, response.statusCode());
        assertEquals(taskManager.getTaskById(taskId), updatedTask);
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        int taskId = taskManager.createNewTask(new Task("Task 1", "Description for task 1", LocalDateTime.now(), Duration.ofMinutes(30)));

        URI url = URI.create(String.format("http://localhost:8080/tasks/%d", taskId));
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.OK.code, response.statusCode());
        assertEquals(0, taskManager.getTasks().size());
        assertThrows(NotFoundTaskException.class, () -> taskManager.getTaskById(taskId));
    }

    @Test
    public void shouldReturnTaskById() throws IOException, InterruptedException {
        int taskId = taskManager.createNewTask(new Task("Task 1", "Description for task 1", LocalDateTime.now(), Duration.ofMinutes(30)));

        URI url = URI.create(String.format("http://localhost:8080/tasks/%d", taskId));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.OK.code, response.statusCode());

        Task taskFromManager = gson.fromJson(JsonParser.parseString(response.body()), Task.class);
        assertEquals(taskManager.getTaskById(taskId), taskFromManager);
    }

    @Test
    public void shouldReturnNotFoundForNotExistingTask() throws IOException, InterruptedException {
        int taskId = taskManager.createNewTask(new Task("Task 1", "Description for task 1", LocalDateTime.now(), Duration.ofMinutes(30)));

        URI url = URI.create(String.format("http://localhost:8080/tasks/%d", taskId + 1));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.NOT_FOUND.code, response.statusCode());
    }

    @Test
    public void shouldNotCreateNewTaskBecauseOfOverlapping() throws IOException, InterruptedException {
        taskManager.createNewTask(new Task("Task 1", "Description for task 1", LocalDateTime.now(), Duration.ofMinutes(30)));
        Task task = new Task("Task 1", "Description for task 1", LocalDateTime.now(), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpStatus.NOT_ACCEPTABLE.code, response.statusCode());
    }
}
