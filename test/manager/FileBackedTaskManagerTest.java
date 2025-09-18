package manager;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileTaskManager;
    private Path tempFile;

    @BeforeEach
    public void createTempFileAndManager() {
        try {
            tempFile = Files.createTempFile("FileManager", ".csv");
            fileTaskManager = new FileBackedTaskManager(tempFile.toString());
        } catch (IOException e) {
            System.out.printf("%s", e);
        }
    }

    @AfterEach
    public void deleteTempFile() {
        try {
            Files.deleteIfExists(tempFile);
        } catch (IOException e) {
            System.out.printf("%s", e);
        }
    }

    @Test
    public void shouldLoadFromEmptyFile() throws ManagerSaveException {
        FileBackedTaskManager fileTaskManager = FileBackedTaskManager.loadFromFile(tempFile.toFile());
        assertEquals(0, fileTaskManager.getSubtasks().size() + fileTaskManager.getEpics().size() + fileTaskManager.getSubtasks().size());
    }

    @Test
    public void shouldSaveTasksIntoFile() throws ManagerSaveException {
        int id1 = fileTaskManager.createNewTask(new Task("Task", "Description for Task", LocalDateTime.now(), Duration.ofMinutes(30)));
        int id2 = fileTaskManager.createNewEpic(new Epic("Epic", "Description for Epic"));
        Integer id3 = fileTaskManager.createNewSubtask(new Subtask("Subtask", "Description for Subtask", LocalDateTime.now(), Duration.ofMinutes(30), id2));

        try (BufferedReader br = new BufferedReader(new FileReader(tempFile.toString()))) {
            int linesCount = 0;
            while (br.ready()) {
                String line = br.readLine();
                linesCount++;
            }
            assertEquals(3, linesCount - 1);
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("%s", e));
        }
    }

    @Test
    public void shouldLoadTasksFromFile() throws ManagerSaveException {
        int id1 = fileTaskManager.createNewTask(new Task("Task", "Description for Task", LocalDateTime.now(), Duration.ofMinutes(30)));
        int id2 = fileTaskManager.createNewEpic(new Epic("Epic", "Description for Epic"));
        Integer id3 = fileTaskManager.createNewSubtask(new Subtask("Subtask", "Description for Subtask", LocalDateTime.now(), Duration.ofMinutes(30), id2));

        FileBackedTaskManager anotherFileTaskManager = FileBackedTaskManager.loadFromFile(tempFile.toFile());
        assertEquals(3, anotherFileTaskManager.getSubtasks().size() + anotherFileTaskManager.getEpics().size() + anotherFileTaskManager.getSubtasks().size());
    }
}