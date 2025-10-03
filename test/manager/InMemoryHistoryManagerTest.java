package manager;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void createManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldExceedTenViewsInHistory() throws ManagerSaveException {
        for (int i = 0; i < 13; i++) {
            Integer taskId = taskManager.createNewTask(new Task(String.format("Task%d", i), String.format("Description for task%d", i), LocalDateTime.now().minusHours(i), Duration.ofMinutes(30)));
        }
        for (Task task : taskManager.getTasks()) {
            Task taskForHistory = taskManager.getTaskById(task.getId());
        }
        assertTrue(taskManager.getHistory().size() > 10);
    }

    @Test
    public void shouldNotContainsDuplicates() throws ManagerSaveException {
        int taskId = taskManager.createNewTask(new Task("Task", "Description", LocalDateTime.now(), Duration.ofMinutes(30)));
        for (int i = 0; i < 13; i++) {
            Task taskForHistory = taskManager.getTaskById(taskId);
        }
        assertEquals(1, taskManager.getHistory().size());
        assertTrue(taskManager.getHistory().contains(taskManager.getTaskById(taskId)));
    }

    @Test
    public void shouldKeepLastTaskVersionInHistory() throws ManagerSaveException {
        int taskId = taskManager.createNewTask(new Task("Task", "Description", LocalDateTime.now(), Duration.ofMinutes(30)));
        Task taskBeforeUpdate = taskManager.getTaskById(taskId);
        taskManager.updateTask(new Task(taskId, "Task", "Description for Task after Update", LocalDateTime.now(), Duration.ofMinutes(30), TaskStatus.NEW));
        Task taskAfterUpdate = taskManager.getTaskById(taskId);

        assertTrue(taskManager.getHistory().contains(taskAfterUpdate));
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    public void shouldRemoveFromHistoryHeadAfterDeleteFromTaskManager() throws ManagerSaveException {
        int taskId = taskManager.createNewTask(new Task("Task", "Description", LocalDateTime.now(), Duration.ofMinutes(30)));
        int task2Id = taskManager.createNewTask(new Task("Task 2", "Description 2", LocalDateTime.now().minusDays(1), Duration.ofMinutes(30)));
        Task taskForHistory = taskManager.getTaskById(taskId);
        taskForHistory = taskManager.getTaskById(task2Id);
        assertEquals(2, taskManager.getHistory().size());

        taskManager.deleteTaskById(task2Id);
        assertEquals(1, taskManager.getHistory().size());
        assertThrows(NullPointerException.class, () -> taskManager.getHistory().contains(taskManager.getTaskById(task2Id)));
        assertTrue(taskManager.getHistory().contains(taskManager.getTaskById(taskId)));
    }

    @Test
    public void shouldRemoveFromHistoryMiddleAfterDeleteFromTaskManager() throws ManagerSaveException {
        int taskId = taskManager.createNewTask(new Task("Task", "Description", LocalDateTime.now(), Duration.ofMinutes(30)));
        int task2Id = taskManager.createNewTask(new Task("Task 2", "Description 2", LocalDateTime.now().minusHours(10), Duration.ofMinutes(30)));
        int task3Id = taskManager.createNewTask(new Task("Task 3", "Description 3", LocalDateTime.now().minusDays(1), Duration.ofMinutes(30)));
        Task taskForHistory = taskManager.getTaskById(taskId);
        taskForHistory = taskManager.getTaskById(task2Id);
        taskForHistory = taskManager.getTaskById(task3Id);
        assertEquals(3, taskManager.getHistory().size());

        taskManager.deleteTaskById(task2Id);
        assertEquals(2, taskManager.getHistory().size());
        assertThrows(NullPointerException.class, () -> taskManager.getHistory().contains(taskManager.getTaskById(task2Id)));
        assertTrue(taskManager.getHistory().contains(taskManager.getTaskById(taskId)));
        assertTrue(taskManager.getHistory().contains(taskManager.getTaskById(task3Id)));
    }

    @Test
    public void shouldRemoveFromHistoryTailAfterDeleteFromTaskManager() throws ManagerSaveException {
        int taskId = taskManager.createNewTask(new Task("Task", "Description", LocalDateTime.now(), Duration.ofMinutes(30)));
        int task2Id = taskManager.createNewTask(new Task("Task 2", "Description 2", LocalDateTime.now().minusHours(10), Duration.ofMinutes(30)));
        int task3Id = taskManager.createNewTask(new Task("Task 3", "Description 3", LocalDateTime.now().minusDays(1), Duration.ofMinutes(30)));
        Task taskForHistory = taskManager.getTaskById(taskId);
        taskForHistory = taskManager.getTaskById(task2Id);
        taskForHistory = taskManager.getTaskById(task3Id);
        assertEquals(3, taskManager.getHistory().size());

        taskManager.deleteTaskById(taskId);
        assertEquals(2, taskManager.getHistory().size());
        assertThrows(NullPointerException.class, () -> taskManager.getHistory().contains(taskManager.getTaskById(taskId)));
        assertTrue(taskManager.getHistory().contains(taskManager.getTaskById(task3Id)));
        assertTrue(taskManager.getHistory().contains(taskManager.getTaskById(task2Id)));
    }

}