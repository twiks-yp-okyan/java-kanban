import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subtasksIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksIds = new ArrayList<>();
    }

    // constructor for updateEpic
    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    public void addSubtask(int subtaskId) {
        this.subtasksIds.add(subtaskId);
    }

    public void removeSubtask(int subtaskId) {
        this.subtasksIds.remove(id);
    }

    public void deleteAllSubtasks() {
        subtasksIds.clear();
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
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
