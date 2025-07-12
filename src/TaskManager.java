import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();


    public void updateEpicStatus(int epicId) {
        Epic epic = epics.getOrDefault(epicId, null);
        if (epic != null) {
            int newCount = 0;
            int doneCount = 0;
            for (int subtaskId : epic.getSubtasksIds()) {
                Subtask epicSubtask = subtasks.get(subtaskId);
                switch (epicSubtask.getStatus()) {
                    case NEW -> newCount++;
                    case DONE -> doneCount++;
                }
            }

            TaskStatus updatedEpicStatus;
            if (newCount == epic.getSubtasksIds().size()) {
                updatedEpicStatus = TaskStatus.NEW;
            } else if (doneCount == epic.getSubtasksIds().size()) {
                updatedEpicStatus = TaskStatus.DONE;
            } else {
                updatedEpicStatus = TaskStatus.IN_PROGRESS;
            }
            epic.setStatus(updatedEpicStatus);
        }
    }

    public HashMap<Integer, Subtask> getEpicSubtasks(int epicId) {
        HashMap<Integer, Subtask> epicSubtasks = new HashMap<>();
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksIds()) {
                epicSubtasks.put(subtaskId, subtasks.get(subtaskId));
            }
        }

        return epicSubtasks;
    }

    public int createNewTask(Task newTask) {
        int newTaskId = newTask.getId();
        tasks.put(newTaskId, newTask);
        return newTaskId;
    }

    public int createNewEpic(Epic newEpic) {
        int newEpicId = newEpic.getId();
        epics.put(newEpicId, newEpic);
        return newEpicId;
    }

    public Integer createNewSubtask(Subtask newSubtask) {
        int createdSubtaskEpicId = newSubtask.getEpicId();
        Epic createdSubtaskEpic = getEpicById(createdSubtaskEpicId);
        // if Epic with provided epicId does not exist
        if (createdSubtaskEpic != null) {
            int newSubtaskId = newSubtask.getId();
            subtasks.put(newSubtaskId, newSubtask);
            createdSubtaskEpic.addSubtask(newSubtaskId);
            updateEpicStatus(createdSubtaskEpicId);
            return newSubtaskId;
        }
        return null;
        // Виталий, Надо ли делать проверку, что у создаваемого сабтаска будет epicId существующего епика?
        // Или при передаче несуществующего epicId надо создать новый пустой эпик?
    }

    public void updateTask(Task updatedTask) {
        int updatedTaskId = updatedTask.getId();
        tasks.put(updatedTaskId, updatedTask);
    }

    public void updateEpic(Epic updatedEpic) {
        int updatedEpicId = updatedEpic.getId();
        epics.put(updatedEpicId, updatedEpic);
    }

    public void updateSubtask(Subtask updatedSubtask) {
        int updatedSubtaskEpicId = updatedSubtask.getEpicId();
        Epic updatedSubtaskEpic = getEpicById(updatedSubtaskEpicId);
        // if Epic with provided epicId does not exist
        if (updatedSubtaskEpic != null) {
            int updatedSubtaskId = updatedSubtask.getId();
            subtasks.put(updatedSubtaskId, updatedSubtask);
            updatedSubtaskEpic.addSubtask(updatedSubtaskId);
            updateEpicStatus(updatedSubtaskEpicId);
        }
        // Виталий, Надо ли делать проверку, что у обновленного сабтаска будет epicId своего епика, а не другого?
        // Или сабтаски могут кочевать между епиками? Мне кажется, что могут
    }

    public Task getTaskById(int id) {
        return tasks.getOrDefault(id, null);
    }

    public Epic getEpicById(int id) {
        return epics.getOrDefault(id, null);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.getOrDefault(id, null);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic deletedEpic = epics.remove(id);
        for (int subtaskId : deletedEpic.getSubtasksIds()) {
            deleteSubtaskById(subtaskId);
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask deletedSubtask = subtasks.remove(id);
        int epicId = deletedSubtask.getEpicId();
        updateEpicStatus(epicId);
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (int epicId : epics.keySet()) {
            Epic epic = epics.get(epicId);
            epic.deleteAllSubtasks();
            updateEpicStatus(epicId);
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }
}
