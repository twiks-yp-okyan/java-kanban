package manager;

import exceptions.NotAcceptedTaskException;
import exceptions.NotFoundTaskException;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idSerial = 1;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        Epic epic = epics.getOrDefault(epicId, null);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksIds()) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }
        }
        return epicSubtasks;
    }

    @Override
    public Integer createNewTask(Task newTask) {
        if (this.isTaskDoNotIntersectWithOthers(newTask)) {
            newTask.setId(this.idSerial);
            newTask.setStatus(TaskStatus.NEW);
            tasks.put(newTask.getId(), newTask);
            incrementTaskId();
            return newTask.getId();
        }
        throw new NotAcceptedTaskException(String.format("Cannot create Task with ID = %d because of interception " +
                "with other tasks in Manager", newTask.getId()));
    }

    @Override
    public int createNewEpic(Epic newEpic) {
        newEpic.setId(this.idSerial);
        newEpic.setStatus(TaskStatus.NEW);
        epics.put(newEpic.getId(), newEpic);
        incrementTaskId();
        return newEpic.getId();
    }

    @Override
    public Integer createNewSubtask(Subtask newSubtask) {
        if (this.isTaskDoNotIntersectWithOthers(newSubtask)) {
            Epic createdSubtaskEpic = epics.getOrDefault(newSubtask.getEpicId(), null);
            // if Epic with provided epicId does not exist
            if (createdSubtaskEpic != null) {
                newSubtask.setId(this.idSerial);
                newSubtask.setStatus(TaskStatus.NEW);
                subtasks.put(newSubtask.getId(), newSubtask);
                createdSubtaskEpic.addSubtask(newSubtask.getId());
                updateEpicData(newSubtask.getEpicId());
                incrementTaskId();
                return newSubtask.getId();
            }
        }
        throw new NotAcceptedTaskException(String.format("Cannot create Subtask with ID = %d because of interception " +
                "with other tasks in Manager", newSubtask.getId()));
    }

    @Override
    public void updateTask(Task updatedTask) {
        Task taskInManager = tasks.getOrDefault(updatedTask.getId(), null);
        if (updatedTask.equals(taskInManager) && this.isTaskDoNotIntersectWithOthers(updatedTask)) {
            tasks.put(updatedTask.getId(), updatedTask);
        } else {
            throw new NotAcceptedTaskException("This Task is not in Manager yet or intersect with others");
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        Epic epicInManager = epics.getOrDefault(updatedEpic.getId(), null);
        if (updatedEpic.equals(epicInManager)) {
            List<Integer> subtasksIds = epicInManager.getSubtasksIds(); // чтобы нельзя было изменить список сабтасков
            int updatedEpicId = updatedEpic.getId();
            epics.put(updatedEpicId, new Epic(updatedEpicId, updatedEpic.getName(), updatedEpic.getDescription(), subtasksIds));
            updateEpicData(updatedEpicId);
        } else {
            throw new NotAcceptedTaskException("This Epic is not in Manager");

        }

    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        Subtask subtaskInManager = subtasks.getOrDefault(updatedSubtask.getId(), null);
        if (updatedSubtask.equals(subtaskInManager) && this.isTaskDoNotIntersectWithOthers(updatedSubtask)) {
            Epic updatedSubtaskEpic = epics.getOrDefault(updatedSubtask.getEpicId(), null);
            // if Epic with provided epicId does not exist
            if (updatedSubtaskEpic != null) {
                subtasks.put(updatedSubtask.getId(), updatedSubtask);
                updateEpicData(updatedSubtask.getEpicId());
            }
        } else {
            throw new NotAcceptedTaskException("This Subtask is not in Manager yet or intersect with others");
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.getOrDefault(id, null);
        if (task != null) {
            historyManager.add(task);
            return task;
        } else {
            throw new NotFoundTaskException(String.format("There is no Task with ID = %d in Manager", id));
        }
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.getOrDefault(id, null);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        } else {
            throw new NotFoundTaskException(String.format("There is no Epic with ID = %d in Manager", id));
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.getOrDefault(id, null);
        if (subtask != null) {
            historyManager.add(subtask);
            return subtask;
        } else {
            throw new NotFoundTaskException(String.format("There is no Subtask with ID = %d in Manager", id));
        }
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = getTaskById(id);
        tasks.remove(task.getId());
        historyManager.remove(task.getId());

    }

    @Override
    public void deleteEpicById(int id) {
        Epic deletedEpic = getEpicById(id);
        epics.remove(id);
        historyManager.remove(id);

        for (int subtaskId : deletedEpic.getSubtasksIds()) {
            deleteSubtaskById(subtaskId);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask deletedSubtask = getSubtaskById(id);
        subtasks.remove(deletedSubtask.getId());
        historyManager.remove(deletedSubtask.getId());

        int epicId = deletedSubtask.getEpicId();
        Epic epic = epics.getOrDefault(epicId, null);
        if (epic != null) {
            epic.removeSubtask(deletedSubtask.getId());
            updateEpicData(epicId);
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
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer taskId : subtasks.keySet()) {
            historyManager.remove(taskId);
        }
        subtasks.clear();

        for (int epicId : epics.keySet()) {
            Epic epic = epics.get(epicId);
            epic.deleteAllSubtasks();
            updateEpicData(epicId);
        }
    }

    @Override
    public void deleteAllEpics() {
        for (Integer taskId : epics.keySet()) {
            historyManager.remove(taskId);
        }
        epics.clear();

        deleteAllSubtasks();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        List<Task> tasksWithStartTime = this.getTasks().stream()
                .filter(task -> task.getStartTime() != null)
                .toList();
        List<Subtask> subtasksWithStartTime = this.getSubtasks().stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .toList();
        prioritizedTasks.addAll(tasksWithStartTime);
        prioritizedTasks.addAll(subtasksWithStartTime);

        return prioritizedTasks;
    }

    // only for load from file
    protected void addTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    protected void addEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    protected void addSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicData(subtask.getEpicId());
        }
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

    private void defineEpicStartTime(int epicId) {
        Epic epic = epics.getOrDefault(epicId, null);
        if (epic != null) {
            Optional<LocalDateTime> minSubtasksStartTime = this.getEpicSubtasks(epicId).stream()
                    .map(Task::getStartTime)
                    .min(LocalDateTime::compareTo);
            minSubtasksStartTime.ifPresentOrElse(epic::setStartTime,
                    () -> epic.setStartTime(null)); // на случай удаления всех сабтасков
        }
    }

    private void defineEpicEndTime(int epicId) {
        Epic epic = epics.getOrDefault(epicId, null);
        if (epic != null) {
            Optional<Subtask> subtaskWithLatestStartTime = this.getEpicSubtasks(epicId).stream()
                    .max(Comparator.comparing(Task::getStartTime));
            if (subtaskWithLatestStartTime.isPresent()) {
                LocalDateTime maxSubtaskStartTime = subtaskWithLatestStartTime.get().getStartTime();
                Duration durationOfLatestSubtask = subtaskWithLatestStartTime.get().getDuration();
                epic.setEndTime(maxSubtaskStartTime.plus(durationOfLatestSubtask));
            } else {
                epic.setEndTime(null); // на случай удаления всех сабтасков
            }
        }
    }

    private void updateEpicData(int epicId) {
        this.updateEpicStatus(epicId);
        this.defineEpicStartTime(epicId);
        this.defineEpicEndTime(epicId);
    }

    private boolean isTwoTasksIntersect(Task task1, Task task2) {
        return task1.getStartTime() != null &&
                task2.getStartTime() != null &&
                !(task1.getStartTime().isAfter(task2.getStartTime().plus(task2.getDuration())) ||
                task2.getStartTime().isAfter(task1.getStartTime().plus(task1.getDuration())));
    }

    private boolean isTaskDoNotIntersectWithOthers(Task task) {
        Optional<Boolean> firstIntersectedTask = this.getPrioritizedTasks().stream()
                .filter(prioritizedTask -> !prioritizedTask.equals(task)) // для update
                .map(prioritizedTask -> this.isTwoTasksIntersect(task, prioritizedTask))
                .filter(isIntersect -> isIntersect)
                .findFirst();

        return firstIntersectedTask.isEmpty();
    }

    private void incrementTaskId() {
        ++this.idSerial;
    }
}
