package manager;

import exceptions.ManagerSaveException;
import task.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    @Override
    public int createNewTask(Task newTask) throws ManagerSaveException {
        int taskId = super.createNewTask(newTask);
        save();
        return taskId;
    }

    @Override
    public int createNewEpic(Epic newEpic) throws ManagerSaveException {
        int epicId = super.createNewEpic(newEpic);
        save();
        return epicId;
    }

    @Override
    public Integer createNewSubtask(Subtask newSubtask) throws ManagerSaveException {
        int subtaskId = super.createNewSubtask(newSubtask);
        save();
        return subtaskId;
    }

    @Override
    public void updateTask(Task updatedTask) throws ManagerSaveException {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) throws ManagerSaveException {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) throws ManagerSaveException {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) throws ManagerSaveException {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) throws ManagerSaveException {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) throws ManagerSaveException {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() throws ManagerSaveException {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() throws ManagerSaveException {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() throws ManagerSaveException {
        super.deleteAllEpics();
        save();
    }

    public void loadFromFile(File file) throws ManagerSaveException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                String currentRow = br.readLine();
                if (!currentRow.startsWith("id")) {
                    Task currentTask = taskFromCSVRow(currentRow);
                    TaskType type = TaskType.valueOf(currentRow.split(",")[1]);
                    switch (type) {
                        case TASK -> super.addTask(currentTask);
                        case EPIC -> super.addEpic((Epic) currentTask);
                        case SUBTASK -> super.addSubtask((Subtask) currentTask);
                    }
                }
            }
            // recover subtask ids list in epics
            for (Subtask subtask : super.getSubtasks()) {
                Epic epicOfCurrentSubtask = super.getEpicById(subtask.getEpicId());
                epicOfCurrentSubtask.addSubtask(subtask.getId());
            }
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("User-defined exception while reading file: %s", e));
        }
    }

    private void save() throws ManagerSaveException {
        final String rowWithColumnNames = "id,type,name,status,description,epic\n";

        try (FileWriter taskFileWriter = new FileWriter("tasks.csv")) {
            taskFileWriter.write(rowWithColumnNames);

            for (TaskType type : TaskType.values()) {
                List<? extends Task> currentTasks = getListOfSpecialTask(type);
                for (Task task : currentTasks) {
                    taskFileWriter.write(taskToCVSRow(task, type));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("User-defined exception while reading file: %s", e));
        }
    }

    private String taskToCVSRow(Task task, TaskType type) {
        Integer epicId = null;
        if (type.equals(TaskType.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            epicId = subtask.getEpicId();
        }
        return String.format("%d,%s,%s,%s,%s,%d%n", task.getId(), type, task.getName(), task.getStatus(), task.getDescription(), epicId);
    }

    private Task taskFromCSVRow(String csvRow) {
        String[] csvRowArray = csvRow.split(",");
        TaskType type = TaskType.valueOf(csvRowArray[1]);
        switch (type) {
            case TASK -> {
                return new Task(Integer.parseInt(csvRowArray[0]), csvRowArray[2], csvRowArray[4], TaskStatus.valueOf(csvRowArray[3]));
            } case EPIC -> {
                return new Epic(Integer.parseInt(csvRowArray[0]), csvRowArray[2], csvRowArray[4], TaskStatus.valueOf(csvRowArray[3]));
            } case SUBTASK -> {
                return new Subtask(Integer.parseInt(csvRowArray[0]), csvRowArray[2], csvRowArray[4], TaskStatus.valueOf(csvRowArray[3]), Integer.parseInt(csvRowArray[5]));
            }
        }
        return null;
    }

    private List<? extends Task> getListOfSpecialTask(TaskType taskType) {
        switch (taskType) {
            case TASK -> {
                return super.getTasks();
            } case EPIC -> {
                return super.getEpics();
            } case SUBTASK -> {
                return super.getSubtasks();
            }
        }
        return new ArrayList<>();
    }
}
