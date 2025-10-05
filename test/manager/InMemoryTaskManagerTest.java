package manager;

import exceptions.NotAcceptedTaskException;
import exceptions.NotFoundTaskException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

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
        Task task = new Task("Task 1", "Description for task 1", LocalDateTime.now(), Duration.ofMinutes(30));

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
        Subtask subtask = new Subtask("Subtask 1", "Description for Subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), epicId);

        int subtaskId = taskManager.createNewSubtask(subtask);
        ArrayList<Subtask> allSubtasks = taskManager.getSubtasks();
        assertTrue(allSubtasks.contains(subtask));

        assertEquals(subtask, taskManager.getSubtaskById(subtaskId));
    }

    @Test
    public void shouldUpdateTaskAndCompareToOriginal() {
        Task task = new Task("Task for update test", "Description", LocalDateTime.now(), Duration.ofMinutes(30));
        int taskId = taskManager.createNewTask(task);
        Task taskInManager = taskManager.getTaskById(taskId);
        Task updatedTask = new Task(taskId, "Task for update test", "Updated description", LocalDateTime.now(), Duration.ofMinutes(30), TaskStatus.NEW);
        taskManager.updateTask(updatedTask);
        Task updatedTaskInManager = taskManager.getTaskById(taskId);

        assertEquals(taskInManager, updatedTaskInManager);
    }

    @Test
    public void shouldUpdateEpicStatusAfterUpdateEpicsSubtaskStatus() {
        int epicId = taskManager.createNewEpic(new Epic("Epic", "Description"));
        int subtaskId = taskManager.createNewSubtask(new Subtask("Subtask 1", "Description for Subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), epicId));
        int subtask2Id = taskManager.createNewSubtask(new Subtask("Subtask 2", "Description for Subtask 2", LocalDateTime.now().minusDays(1), Duration.ofMinutes(30), epicId));

        taskManager.updateSubtask(new Subtask(subtaskId, "Subtask 1", "Description for Subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), TaskStatus.IN_PROGRESS, epicId));
        taskManager.updateSubtask(new Subtask(subtask2Id, "Subtask 2", "Description for Subtask 2", LocalDateTime.now().minusDays(1), Duration.ofMinutes(30), TaskStatus.DONE, epicId));

        Epic epic = taskManager.getEpicById(epicId);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldCheckEpicNEWStatusWithNEWSubtasks() {
        int epicId = taskManager.createNewEpic(new Epic("Epic", "Description"));
        int subtaskId = taskManager.createNewSubtask(new Subtask("Subtask 1", "Description for Subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), epicId));
        int subtask2Id = taskManager.createNewSubtask(new Subtask("Subtask 2", "Description for Subtask 2", LocalDateTime.now().minusDays(1), Duration.ofMinutes(30), epicId));
        Epic epic = taskManager.getEpicById(epicId);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldCheckEpicDONEStatusWithDONESubtasks() {
        int epicId = taskManager.createNewEpic(new Epic("Epic", "Description"));
        int subtaskId = taskManager.createNewSubtask(new Subtask("Subtask 1", "Description for Subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), epicId));
        int subtask2Id = taskManager.createNewSubtask(new Subtask("Subtask 2", "Description for Subtask 2", LocalDateTime.now().minusDays(1), Duration.ofMinutes(30), epicId));

        taskManager.updateSubtask(new Subtask(subtaskId, "Subtask 1", "Description for Subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), TaskStatus.DONE, epicId));
        taskManager.updateSubtask(new Subtask(subtask2Id, "Subtask 2", "Description for Subtask 2", LocalDateTime.now().minusDays(1), Duration.ofMinutes(30), TaskStatus.DONE, epicId));
        Epic epic = taskManager.getEpicById(epicId);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void shouldCheckEpicStatusWithNEWAndDONESubtasks() {
        int epicId = taskManager.createNewEpic(new Epic("Epic", "Description"));
        int subtaskId = taskManager.createNewSubtask(new Subtask("Subtask 1", "Description for Subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), epicId));
        int subtask2Id = taskManager.createNewSubtask(new Subtask("Subtask 2", "Description for Subtask 2", LocalDateTime.now().minusDays(1), Duration.ofMinutes(30), epicId));

        taskManager.updateSubtask(new Subtask(subtask2Id, "Subtask 2", "Description for Subtask 2", LocalDateTime.now().minusDays(1), Duration.ofMinutes(30), TaskStatus.DONE, epicId));
        Epic epic = taskManager.getEpicById(epicId);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldCheckEpicINPROGRESSStatusWithINPROGRESSSubtasks() {
        int epicId = taskManager.createNewEpic(new Epic("Epic", "Description"));
        int subtaskId = taskManager.createNewSubtask(new Subtask("Subtask 1", "Description for Subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), epicId));
        int subtask2Id = taskManager.createNewSubtask(new Subtask("Subtask 2", "Description for Subtask 2", LocalDateTime.now().minusDays(1), Duration.ofMinutes(30), epicId));

        taskManager.updateSubtask(new Subtask(subtaskId, "Subtask 1", "Description for Subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), TaskStatus.IN_PROGRESS, epicId));
        taskManager.updateSubtask(new Subtask(subtask2Id, "Subtask 2", "Description for Subtask 2", LocalDateTime.now().minusDays(1), Duration.ofMinutes(30), TaskStatus.IN_PROGRESS, epicId));
        Epic epic = taskManager.getEpicById(epicId);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldReturnNullAndNotContainsInTasksAfterDeleteTaskById() {
        int taskId = taskManager.createNewTask(new Task("Task", "Description", LocalDateTime.now(), Duration.ofMinutes(30)));
        Task task = taskManager.getTaskById(taskId);

        taskManager.deleteTaskById(taskId);
        ArrayList<Task> allTasks = taskManager.getTasks();
        assertFalse(allTasks.contains(task));

        assertThrows(NotFoundTaskException.class, () -> taskManager.getTaskById(taskId));
    }

    @Test
    public void shouldRevertToNewAfterDeleteAllSubtasks() {
        int epicId = taskManager.createNewEpic(new Epic("Epic", "Description"));
        int subtaskId = taskManager.createNewSubtask(new Subtask("Subtask 1", "Description for Subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), epicId));
        Integer subtask2Id = taskManager.createNewSubtask(new Subtask("Subtask 2", "Description for Subtask 2", LocalDateTime.now().minusHours(1), Duration.ofMinutes(30), epicId));

        int epic2Id = taskManager.createNewEpic(new Epic("Epic 2", "Description for epic 2"));
        Integer subtask3Id = taskManager.createNewSubtask(new Subtask("Subtask 3", "Description for Subtask 3", LocalDateTime.now().minusHours(2), Duration.ofMinutes(30), epic2Id));
        // update Subtask status -> update epic status
        taskManager.updateSubtask(new Subtask(subtaskId, "Subtask 1", "Description for Subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), TaskStatus.IN_PROGRESS, epicId));
        taskManager.updateSubtask(new Subtask(subtask2Id, "Subtask 2", "Description for Subtask 2", LocalDateTime.now().minusHours(1), Duration.ofMinutes(30), TaskStatus.DONE, epicId));

        taskManager.updateSubtask(new Subtask(subtask3Id, "Subtask 3", "Description for Subtask 3", LocalDateTime.now().minusHours(2), Duration.ofMinutes(30), TaskStatus.DONE, epic2Id));
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

    @Test
    public void shouldRemoveSubtaskIdFromEpicAfterSubtaskDelete() {
        int epicId = taskManager.createNewEpic(new Epic("Epic", "Description"));
        int subtaskId = taskManager.createNewSubtask(new Subtask("Subtask 1", "Description for Subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), epicId));
        Integer subtask2Id = taskManager.createNewSubtask(new Subtask("Subtask 2", "Description for Subtask 2", LocalDateTime.now().minusHours(1), Duration.ofMinutes(30), epicId));

        taskManager.deleteSubtaskById(subtaskId);
        assertFalse(taskManager.getEpicSubtasks(epicId).contains(subtaskId));
    }

    @Test
    public void shouldDoNotAddTaskBecauseOfIntersection() {
        Integer taskId = taskManager.createNewTask(new Task("Task", "Description", LocalDateTime.now(), Duration.ofMinutes(30)));
        try {
            Integer task2Id = taskManager.createNewTask(new Task("Task", "Description", LocalDateTime.now().minusHours(1), Duration.ofMinutes(90)));
        } catch (NotAcceptedTaskException e) {
            assertEquals(1, taskManager.getTasks().size());
        }
    }

    @Test
    public void shouldReturnPrioritizedTasks() {
        Integer taskId = taskManager.createNewTask(new Task("Task", "Description", LocalDateTime.now(), Duration.ofMinutes(30)));
        Integer task2Id = taskManager.createNewTask(new Task("Task 2", "Description 2", LocalDateTime.now().minusHours(1), Duration.ofMinutes(30)));
        int epicId = taskManager.createNewEpic(new Epic("Epic", "Description"));
        Integer subtaskId = taskManager.createNewSubtask(new Subtask("Subtask", "Description for Subtask", LocalDateTime.now().minusHours(5), Duration.ofMinutes(60), epicId));

        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        Optional<Task> firstTask = prioritizedTasks.stream().findFirst();
        firstTask.ifPresent(task -> assertEquals(subtaskId, task.getId()));
    }

}