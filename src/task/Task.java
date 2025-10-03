package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.status = TaskStatus.NEW;
    }

    // constructor for update Task & Subtask
    public Task(int id, String name, String description, LocalDateTime startTime, Duration duration, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.status = status;
    }

    // constructor for create Epic
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    // constructor for update Epic
    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // constructor for deserialisation from file
    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task anotherTask = (Task) obj;
        return this.id == anotherTask.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration.toMinutes() + '\'' +
                '}';
    }
}
