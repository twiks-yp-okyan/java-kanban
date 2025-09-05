package task;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksIds = new ArrayList<>();
    }

    // constructor for update task.Epic
    public Epic(int id, String name, String description, ArrayList<Integer> subtasksIds) {
        super(id, name, description);
        this.subtasksIds = subtasksIds;
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        this.subtasksIds = new ArrayList<>();
    }

    public void addSubtask(int subtaskId) {
        this.subtasksIds.add(subtaskId);
    }

    public void removeSubtask(Integer subtaskId) {
        this.subtasksIds.remove(subtaskId);
    }

    public void deleteAllSubtasks() {
        subtasksIds.clear();
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + id + '\'' +
                ", subtasksIds='" + subtasksIds + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
