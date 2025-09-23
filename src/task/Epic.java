package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksIds = new ArrayList<>();
    }

    // constructor for update Epic
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

    public void setStartTime(LocalDateTime startTime) {
        super.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        // calculate duration
        if (this.startTime != null && endTime != null) {
            this.duration = Duration.between(this.startTime, this.endTime);
        } else {
            this.duration = null;
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + id + '\'' +
                ", subtasksIds='" + subtasksIds + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
