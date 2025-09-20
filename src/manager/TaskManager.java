package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    List<Task> getHistory();

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    Integer createNewTask(Task newTask);

    int createNewEpic(Epic newEpic);

    Integer createNewSubtask(Subtask newSubtask);

    void updateTask(Task updatedTask);

    void updateEpic(Epic updatedEpic);

    void updateSubtask(Subtask updatedSubtask);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Set<Task> getPrioritizedTasks();
}
