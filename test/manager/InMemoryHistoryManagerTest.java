package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void createManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldExceedTenViewsInHistory() {
        for (int i = 0; i < 13; i++) {
            int taskId = taskManager.createNewTask(new Task("Task " + i, "Description for task " + i));
        }
        for (Task task : taskManager.getTasks()) {
            Task taskForHistory = taskManager.getTaskById(task.getId());
        }
        assertTrue(taskManager.getHistory().size() > 10);
    }

    @Test
    public void shouldNotContainsDuplicates() {
        int taskId = taskManager.createNewTask(new Task("Task", "Description"));
        for (int i = 0; i < 13; i++) {
            Task taskForHistory = taskManager.getTaskById(taskId);
        }
        assertEquals(1, taskManager.getHistory().size());
        assertTrue(taskManager.getHistory().contains(taskManager.getTaskById(taskId)));
    }

    @Test
    public void shouldKeepLastTaskVersionInHistory() {
        int taskId = taskManager.createNewTask(new Task("Task", "Description"));
        Task taskBeforeUpdate = taskManager.getTaskById(taskId);
        taskManager.updateTask(new Task(taskId, "Task", "Description for Task after Update"));
        Task taskAfterUpdate = taskManager.getTaskById(taskId);

        assertTrue(taskManager.getHistory().contains(taskAfterUpdate));
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    public void shouldRemoveFromHistoryAfterDeleteFromTaskManager() {
        int taskId = taskManager.createNewTask(new Task("Task", "Description"));
        int task2Id = taskManager.createNewTask(new Task("Task 2", "Description 2"));
        Task taskForHistory = taskManager.getTaskById(taskId);
        taskForHistory = taskManager.getTaskById(task2Id);
        assertEquals(2, taskManager.getHistory().size());

        taskManager.deleteTaskById(taskId);
        assertEquals(1, taskManager.getHistory().size());
        assertFalse(taskManager.getHistory().contains(taskManager.getTaskById(taskId)));
        assertTrue(taskManager.getHistory().contains(taskManager.getTaskById(task2Id)));
    }

}