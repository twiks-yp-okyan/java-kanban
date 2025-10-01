package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

//    public Subtask(String name, String description, LocalDateTime startTime, Duration duration, int epicId) {
//        super(name, description, startTime, duration);
//        this.epicId = epicId;
//    }

    // constructor for updateSubtask
    public Subtask(int id, String name, String description, LocalDateTime startTime, Duration duration, TaskStatus status, int epicId) {
        super(id, name, description, startTime, duration, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id='" + id + '\'' +
                ", epicId='" + epicId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration.toMinutes() + '\'' +
                '}';
    }
}
