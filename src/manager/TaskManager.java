package manager;

import exceptions.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    int createNewTask(Task newTask) throws ManagerSaveException;

    int createNewEpic(Epic newEpic) throws ManagerSaveException;

    Integer createNewSubtask(Subtask newSubtask) throws ManagerSaveException;

    void updateTask(Task updatedTask) throws ManagerSaveException;

    void updateEpic(Epic updatedEpic) throws ManagerSaveException;

    void updateSubtask(Subtask updatedSubtask) throws ManagerSaveException;

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void deleteTaskById(int id) throws ManagerSaveException;

    void deleteEpicById(int id) throws ManagerSaveException;

    void deleteSubtaskById(int id) throws ManagerSaveException;

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    void deleteAllTasks() throws ManagerSaveException;

    void deleteAllSubtasks() throws ManagerSaveException;

    void deleteAllEpics() throws ManagerSaveException;
}
