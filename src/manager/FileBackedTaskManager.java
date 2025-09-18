package manager;

import exceptions.ManagerSaveException;
import task.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String filename;

    public FileBackedTaskManager(String filename) {
        this.filename = filename;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.getName());
        manager.recoverManagerStateFromFile(file);
        return manager;
    }

    @Override
    public int createNewTask(Task newTask) {
        int taskId = super.createNewTask(newTask);
        save();
        return taskId;
    }

    @Override
    public int createNewEpic(Epic newEpic) {
        int epicId = super.createNewEpic(newEpic);
        save();
        return epicId;
    }

    @Override
    public Integer createNewSubtask(Subtask newSubtask) {
        int subtaskId = super.createNewSubtask(newSubtask);
        save();
        return subtaskId;
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    private void recoverManagerStateFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                String currentRow = br.readLine();
                if (!currentRow.startsWith("id")) {
                    Task currentTask = taskFromCSVRow(currentRow);
                    TaskType type = TaskType.valueOf(currentRow.split(",")[1]);
                    switch (type) {
                        case TASK -> super.addTask(currentTask);
                        case EPIC -> super.addEpic((Epic) currentTask);
                        case SUBTASK -> {
                            Epic subtaskEpic = super.getEpicById(((Subtask) currentTask).getEpicId());
                            subtaskEpic.addSubtask(currentTask.getId());
                            super.addSubtask((Subtask) currentTask);
                        }
                    }
                    // recover history (очень сомнительный признак просмотра из ТЗ - статус не NEW)
                    if (currentTask.getStatus() != TaskStatus.NEW) super.historyManager.add(currentTask);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("User-defined exception while reading file: %s", e));
        }
    }

    private void save() {
        final String rowWithColumnNames = "id,type,name,status,description,start-time,duration,epic\n";

        try (FileWriter taskFileWriter = new FileWriter(filename)) {
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
        return String.format("%d,%s,%s,%s,%s,%s,%s,%d%n",
                task.getId(),
                type,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                (task.getStartTime() != null) ? task.getStartTime() : null,
                (task.getDuration() != null) ? task.getDuration().toMinutes() : null,
                epicId);
    }

    private Task taskFromCSVRow(String csvRow) {
        String[] csvRowArray = csvRow.split(",");
        TaskType type = TaskType.valueOf(csvRowArray[1]);
        switch (type) {
            case TASK -> {
                return new Task(Integer.parseInt(csvRowArray[0]),
                        csvRowArray[2],
                        csvRowArray[4],
                        (csvRowArray[5].equals("null")) ? null : LocalDateTime.parse(csvRowArray[5], DateTimeFormatter.ISO_DATE_TIME),
                        (csvRowArray[6].equals("null")) ? null : Duration.ofMinutes(Integer.parseInt(csvRowArray[6])),
                        TaskStatus.valueOf(csvRowArray[3]));
            } case EPIC -> {
                return new Epic(Integer.parseInt(csvRowArray[0]),
                        csvRowArray[2],
                        csvRowArray[4],
                        TaskStatus.valueOf(csvRowArray[3]));
            } case SUBTASK -> {
                return new Subtask(Integer.parseInt(csvRowArray[0]),
                        csvRowArray[2],
                        csvRowArray[4],
                        (csvRowArray[5].equals("null")) ? null : LocalDateTime.parse(csvRowArray[5], DateTimeFormatter.ISO_DATE_TIME),
                        (csvRowArray[6].equals("null")) ? null : Duration.ofMinutes(Integer.parseInt(csvRowArray[6])),
                        TaskStatus.valueOf(csvRowArray[3]),
                        Integer.parseInt(csvRowArray[7]));
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
