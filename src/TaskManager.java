import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idSerial = 1;

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksIds()) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }
        }
        return epicSubtasks;
    }

    public int createNewTask(Task newTask) {
        newTask.setId(this.idSerial);
        tasks.put(newTask.getId(), newTask);
        incrementTaskId();
        return newTask.getId();
    }

    public int createNewEpic(Epic newEpic) {
        newEpic.setId(this.idSerial);
        epics.put(newEpic.getId(), newEpic);
        incrementTaskId();
        return newEpic.getId();
    }

    public Integer createNewSubtask(Subtask newSubtask) {
        int createdSubtaskEpicId = newSubtask.getEpicId();
        Epic createdSubtaskEpic = getEpicById(createdSubtaskEpicId);
        // if Epic with provided epicId does not exist
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

    public void updateTask(Task updatedTask) {
        int updatedTaskId = updatedTask.getId();
        tasks.put(updatedTaskId, updatedTask);
    }

    public void updateEpic(Epic updatedEpic) {
        int updatedEpicId = updatedEpic.getId();
        epics.put(updatedEpicId, updatedEpic);
        updateEpicStatus(updatedEpicId);
    }

    public void updateSubtask(Subtask updatedSubtask) {
        int updatedSubtaskEpicId = updatedSubtask.getEpicId();
        Epic updatedSubtaskEpic = getEpicById(updatedSubtaskEpicId);
        // if Epic with provided epicId does not exist
        if (updatedSubtaskEpic != null) {
            int updatedSubtaskId = updatedSubtask.getId();
            subtasks.put(updatedSubtaskId, updatedSubtask);
            updateEpicStatus(updatedSubtaskEpicId);
        }
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
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            epic.removeSubtask(id);
            updateEpicStatus(epicId);
        }
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
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
