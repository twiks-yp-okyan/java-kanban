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
    public void shouldIncrementHistorySize() {
        int taskId = taskManager.createNewTask(new Task("Task", "Description"));
        Task task = taskManager.getTaskById(taskId);

        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    public void shouldNotExceedsTenViewsInHistory() {
        int taskId = taskManager.createNewTask(new Task("Task", "Description"));
        for (int i = 0; i < 13; i++) {
            Task task = taskManager.getTaskById(taskId);
        }

        assertEquals(10, taskManager.getHistory().size());
    }

    @Test
    public void shouldContainsInHistoryTaskBeforeUpdate() {
        int taskId = taskManager.createNewTask(new Task("Task", "Description"));
        Task taskBeforeUpdate = taskManager.getTaskById(taskId);
        taskManager.updateTask(new Task(taskId, "Task", "Description for Task after Update"));
        Task taskAfterUpdate = taskManager.getTaskById(taskId);
        List<Task> history = taskManager.getHistory();

        assertEquals(2, history.size());
        assertNotEquals(history.get(0).getDescription(), history.get(1).getDescription());

    }

}