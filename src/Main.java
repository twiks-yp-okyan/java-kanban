import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();
        Integer currentId = TaskManager.getIdSerial();
        // create tests
        int testTaskId = taskManager.createNewTask(new Task(currentId, "Test Task", "Description for test task"));
        currentId = TaskManager.getIdSerial();
        int anotherTestTaskId = taskManager.createNewTask(new Task(currentId,"Another test task", "Another description"));
        currentId = TaskManager.getIdSerial();
        int task3Id = taskManager.createNewTask(new Task(currentId, "Task 3", "Description for Task 3"));

        currentId = TaskManager.getIdSerial();
        int testEpicId = taskManager.createNewEpic(new Epic(currentId, "Test Epic", "Description for test Epic"));
        currentId = TaskManager.getIdSerial();
        int epic2Id = taskManager.createNewEpic(new Epic(currentId, "Epic #2", "Description for Epic#2"));

        currentId = TaskManager.getIdSerial();
        Integer testSubtaskId = taskManager.createNewSubtask(new Subtask(currentId, "Test Subtask", "Description of test subtask", testEpicId));
        currentId = TaskManager.getIdSerial();
        Integer subtask2Id = taskManager.createNewSubtask(new Subtask(currentId, "Subtask 2", "Desc for subtask 2", testEpicId));
        // check create
        System.out.println("##### CREATE TESTS #####");
        printAllTasks(taskManager.getTasks());
        printAllEpics(taskManager.getEpics());
        printAllSubtasks(taskManager.getSubtasks());

        // get & update tests
        int taskIdForUpdateId = taskManager.getTaskById(3).getId();
        taskManager.updateTask(new Task(taskIdForUpdateId, "Updated Task 3", "Updated description for Task 3", TaskStatus.IN_PROGRESS));
        currentId = TaskManager.getIdSerial();
        int task4Id = taskManager.createNewTask(new Task(currentId, "Task 4", "Description for Task 4"));
        System.out.println("##### TASK UPD #####");
        printAllTasks(taskManager.getTasks());

        currentId = TaskManager.getIdSerial();
        int subtask3Id = taskManager.createNewSubtask(new Subtask(currentId, "Subtask #3", "Description for Subtask #3", epic2Id));
        int getEpicForUpdateId = taskManager.getEpicById(5).getId();
        ArrayList<Integer> epicForUpdateSubtasksIds = taskManager.getEpicById(getEpicForUpdateId).getSubtasksIds();
        taskManager.updateEpic(new Epic(getEpicForUpdateId, "Epic #2 updated", "Updated Description for Epic#2", epicForUpdateSubtasksIds));
        System.out.println("##### EPIC UPD #####");
        printAllEpics(taskManager.getEpics());

        int getSubtaskForUpdateId = taskManager.getSubtaskById(9).getId();
        taskManager.updateSubtask(new Subtask(getSubtaskForUpdateId, "Subtask #3 updated", "Updated Description for Subtask #3", TaskStatus.IN_PROGRESS, epic2Id));
        taskManager.updateSubtask(new Subtask(6, "Test Subtask updated", "Updated Description of test subtask", TaskStatus.DONE, testEpicId));
        taskManager.updateSubtask(new Subtask(7, "Subtask 2 updated", "Updated Desc for subtask 2", TaskStatus.DONE, testEpicId));
        System.out.println("##### SUBTASK UPD #####");
        printAllSubtasks(taskManager.getSubtasks());
        printAllEpics(taskManager.getEpics());

        // getAllEpicSubtasks
        ArrayList<Subtask> epicSubtasks = taskManager.getEpicSubtasks(4);
        System.out.println("-----epic subtasks-----");
        for (Subtask epicSubtask : epicSubtasks) {
            System.out.println(epicSubtask);
        }

        // delete tests
        taskManager.deleteTaskById(2);
        System.out.println("##### TASK DELETE #####");
        printAllTasks(taskManager.getTasks());

        taskManager.deleteSubtaskById(7);
        taskManager.deleteSubtaskById(9);
        System.out.println("##### SUBTASK DELETE #####");
        printAllSubtasks(taskManager.getSubtasks());
        printAllEpics(taskManager.getEpics());

        taskManager.deleteEpicById(4);
        System.out.println("##### DELETE TESTS #####");
        printAllTasks(taskManager.getTasks());
        printAllEpics(taskManager.getEpics());
        printAllSubtasks(taskManager.getSubtasks());

    }

    public static void printAllTasks(ArrayList<Task> allTasks) {
        for (Task task : allTasks) {
            System.out.println(task);
        }
    }

    public static void printAllEpics(ArrayList<Epic> allEpics) {
        for (Epic epic : allEpics) {
            System.out.println(epic);
        }
    }

    public static void printAllSubtasks(ArrayList<Subtask> allSubtasks) {
        for (Subtask subtask : allSubtasks) {
            System.out.println(subtask);
        }
    }
}
