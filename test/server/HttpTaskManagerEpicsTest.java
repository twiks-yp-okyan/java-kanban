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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpTaskManagerEpicsTest {
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
    public void shouldDeleteAllEpicSubtasksAfterEpicDelete() throws IOException, InterruptedException {
        int epicId = taskManager.createNewEpic(new Epic("Epic", "Description"));
        int subtaskId = taskManager.createNewSubtask(new Subtask("Subtask", "Description for subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), 1));
        int subtask1Id = taskManager.createNewSubtask(new Subtask("Subtask 1", "Description for subtask 1", LocalDateTime.now().minusHours(1), Duration.ofMinutes(30), 1));
        int subtask2Id = taskManager.createNewSubtask(new Subtask("Subtask 2", "Description for subtask 2", LocalDateTime.now().plusHours(1), Duration.ofMinutes(30), 1));

        URI url = URI.create(String.format("http://localhost:8080/epics/%d", epicId));
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.OK.code, response.statusCode());
        assertThrows(NullPointerException.class, () -> taskManager.getEpicById(epicId));
        assertThrows(NullPointerException.class, () -> taskManager.getSubtaskById(subtaskId));
        assertThrows(NullPointerException.class, () -> taskManager.getSubtaskById(subtask1Id));
        assertThrows(NullPointerException.class, () -> taskManager.getSubtaskById(subtask2Id));
    }
}
