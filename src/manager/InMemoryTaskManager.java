package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idSerial = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epics.getOrDefault(epicId, null); //getEpicById(epicId); - пришлось заменить на метод коллекции для корректной работы истории просмотров
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksIds()) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }
        }
        return epicSubtasks;
    }

    @Override
    public int createNewTask(Task newTask) {
        newTask.setId(this.idSerial);
        tasks.put(newTask.getId(), newTask);
        incrementTaskId();
        return newTask.getId();
    }

    @Override
    public int createNewEpic(Epic newEpic) {
        newEpic.setId(this.idSerial);
        epics.put(newEpic.getId(), newEpic);
        incrementTaskId();
        return newEpic.getId();
    }

    @Override
    public Integer createNewSubtask(Subtask newSubtask) {
        int createdSubtaskEpicId = newSubtask.getEpicId();
        Epic createdSubtaskEpic = epics.getOrDefault(createdSubtaskEpicId, null); //getEpicById(createdSubtaskEpicId); - аналогично
        // if task.Epic with provided epicId does not exist
        if (createdSubtaskEpic != null) {
            newSubtask.setId(this.idSerial);
            subtasks.put(newSubtask.getId(), newSubtask);
            createdSubtaskEpic.addSubtask(newSubtask.getId());
            updateEpicStatus(createdSubtaskEpicId);
            incrementTaskId();
            return newSubtask.getId();
        }
        return null;
    }

    @Override
    public void updateTask(Task updatedTask) {
        int updatedTaskId = updatedTask.getId();
        tasks.put(updatedTaskId, updatedTask);
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        int updatedEpicId = updatedEpic.getId();
        epics.put(updatedEpicId, updatedEpic);
        updateEpicStatus(updatedEpicId);
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        int updatedSubtaskEpicId = updatedSubtask.getEpicId();
        Epic updatedSubtaskEpic = epics.getOrDefault(updatedSubtaskEpicId, null); // getEpicById(updatedSubtaskEpicId); - аналогично
        // if task.Epic with provided epicId does not exist
        if (updatedSubtaskEpic != null) {
            int updatedSubtaskId = updatedSubtask.getId();
            subtasks.put(updatedSubtaskId, updatedSubtask);
            updateEpicStatus(updatedSubtaskEpicId);
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.getOrDefault(id, null);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.getOrDefault(id, null);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.getOrDefault(id, null);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic deletedEpic = epics.remove(id);
        for (int subtaskId : deletedEpic.getSubtasksIds()) {
            deleteSubtaskById(subtaskId);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask deletedSubtask = subtasks.remove(id);
        int epicId = deletedSubtask.getEpicId();
        Epic epic = epics.getOrDefault(epicId, null); // getEpicById(epicId); - аналогично
        if (epic != null) {
            epic.removeSubtask(id);
            updateEpicStatus(epicId);
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (int epicId : epics.keySet()) {
            Epic epic = epics.get(epicId);
            epic.deleteAllSubtasks();
            updateEpicStatus(epicId);
        }
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    private void updateEpicStatus(int epicId) {
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

    private void incrementTaskId() {
        ++this.idSerial;
    }
}
