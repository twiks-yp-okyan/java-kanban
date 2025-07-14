import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Integer> subtasksIds;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subtasksIds = new ArrayList<>();
    }

    // constructor for update Epic
    // Виталий, пришлось передавать явно список айди сабтасков. Это ок?
    public Epic(int id, String name, String description, ArrayList<Integer> subtasksIds) {
        super(id, name, description);
        this.subtasksIds = subtasksIds;
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
