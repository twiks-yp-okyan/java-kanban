package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void createManager() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldReturnInitializedInstanceOfTaskManager() {
        TaskManager taskManagerLocal = Managers.getDefault();
        assertNotNull(taskManagerLocal);
    }

    @Test
    public void shouldReturnInitializedInstanceOfHistoryManager() {
        HistoryManager historyManagerLocal = Managers.getDefaultHistory();
        assertNotNull(historyManagerLocal);
    }

    @Test
    public void shouldAddTaskAndGetItById() {
        Task task = new Task("Task 1", "Description for task 1");

        int taskId = taskManager.createNewTask(task);
        ArrayList<Task> allTasks = taskManager.getTasks();
        assertTrue(allTasks.contains(task));

        assertEquals(task, taskManager.getTaskById(taskId));
    }

    @Test
    public void shouldAddEpicAndGetItById() {
        Epic epic = new Epic("Epic 1", "Description for Epic 1");

        int epicId = taskManager.createNewEpic(epic);
        ArrayList<Epic> allEpics = taskManager.getEpics();
        assertTrue(allEpics.contains(epic));

        assertEquals(epic, taskManager.getEpicById(epicId));
    }

    @Test
    public void shouldAddSubtaskAndGetItById() {
        int epicId = taskManager.createNewEpic(new Epic("Epic", "Description"));
        Subtask subtask = new Subtask("Subtask 1", "Description for Subtask 1", epicId);

        int subtaskId = taskManager.createNewSubtask(subtask);
        ArrayList<Subtask> allSubtasks = taskManager.getSubtasks();
        assertTrue(allSubtasks.contains(subtask));

        assertEquals(subtask, taskManager.getSubtaskById(subtaskId));
    }

    @Test
    public void shouldUpdateTaskAndCompareToOriginal() {
        Task task = new Task("Task for update test", "Description");
        int taskId = taskManager.createNewTask(task);
        Task taskInManager = taskManager.getTaskById(taskId);
        Task updatedTask = new Task(taskId, "Task for update test", "Updated description");
        taskManager.updateTask(updatedTask);
        Task updatedTaskInManager = taskManager.getTaskById(taskId);

        assertEquals(taskInManager, updatedTaskInManager);
    }

    @Test
    public void shouldUpdateEpicStatusAfterUpdateEpicsSubtaskStatus() {
        int epicId = taskManager.createNewEpic(new Epic("Epic", "Description"));
        int subtaskId = taskManager.createNewSubtask(new Subtask("Subtask 1", "Description for Subtask 1", epicId));
        int subtask2Id = taskManager.createNewSubtask(new Subtask("Subtask 2", "Description for Subtask 2", epicId));

        taskManager.updateSubtask(new Subtask(subtaskId, "Subtask 1", "Description for Subtask 1", TaskStatus.IN_PROGRESS, epicId));
        taskManager.updateSubtask(new Subtask(subtask2Id, "Subtask 2", "Description for Subtask 2", TaskStatus.DONE, epicId));

        Epic epic = taskManager.getEpicById(epicId);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldReturnNullAndNotContainsInTasksAfterDeleteTaskById() {
        int taskId = taskManager.createNewTask(new Task("Task", "Description"));
        Task task = taskManager.getTaskById(taskId);

        taskManager.deleteTaskById(taskId);
        ArrayList<Task> allTasks = taskManager.getTasks();
        assertFalse(allTasks.contains(task));

        assertNull(taskManager.getTaskById(taskId));
    }

    @Test
    public void shouldRevertToNewAfterDeleteAllSubtasks() {
        int epicId = taskManager.createNewEpic(new Epic("Epic", "Description"));
        int subtaskId = taskManager.createNewSubtask(new Subtask("Subtask 1", "Description for Subtask 1", epicId));
        int subtask2Id = taskManager.createNewSubtask(new Subtask("Subtask 2", "Description for Subtask 2", epicId));

        int epic2Id = taskManager.createNewEpic(new Epic("Epic 2", "Description for epic 2"));
        int subtask3Id = taskManager.createNewSubtask(new Subtask("Subtask 3", "Description for Subtask 3", epic2Id));
        // update Subtask status -> update epic status
        taskManager.updateSubtask(new Subtask(subtaskId, "Subtask 1", "Description for Subtask 1", TaskStatus.IN_PROGRESS, epicId));
        taskManager.updateSubtask(new Subtask(subtask2Id, "Subtask 2", "Description for Subtask 2", TaskStatus.DONE, epicId));

        taskManager.updateSubtask(new Subtask(subtask3Id, "Subtask 3", "Description for Subtask 3", TaskStatus.DONE, epic2Id));
        // check epics statuses
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epicId).getStatus());
        assertEquals(TaskStatus.DONE, taskManager.getEpicById(epic2Id).getStatus());
        // delete all subtasks & REcheck epics statuses
        taskManager.deleteAllSubtasks();
        assertEquals(TaskStatus.NEW, taskManager.getEpicById(epicId).getStatus());
        assertEquals(TaskStatus.NEW, taskManager.getEpicById(epic2Id).getStatus());
        // check empty of subtasks collection in manager
        assertEquals(0, taskManager.getSubtasks().size());
    }

}