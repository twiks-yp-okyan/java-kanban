import manager.InMemoryTaskManager;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        TaskManager taskManager = new InMemoryTaskManager();
        // create tests
        int testTaskId = taskManager.createNewTask(new Task("Test task.Task", "Description for test task"));
        int anotherTestTaskId = taskManager.createNewTask(new Task("Another test task", "Another description"));
        int task3Id = taskManager.createNewTask(new Task("task.Task 3", "Description for task.Task 3"));

        int testEpicId = taskManager.createNewEpic(new Epic("Test task.Epic", "Description for test task.Epic"));
        int epic2Id = taskManager.createNewEpic(new Epic("task.Epic #2", "Description for task.Epic#2"));

        Integer testSubtaskId = taskManager.createNewSubtask(new Subtask("Test task.Subtask", "Description of test subtask", testEpicId));
        Integer subtask2Id = taskManager.createNewSubtask(new Subtask("task.Subtask 2", "Desc for subtask 2", testEpicId));
        // check create
        System.out.println("##### CREATE TESTS #####");
        printAllTasks(taskManager.getTasks());
        printAllEpics(taskManager.getEpics());
        printAllSubtasks(taskManager.getSubtasks());

        // get & update tests
        int taskIdForUpdateId = taskManager.getTaskById(3).getId();

        System.out.println("##### VIEW HISTORY #####");
        printViewHistory(taskManager.getHistory());


        taskManager.updateTask(new Task(taskIdForUpdateId, "Updated task.Task 3", "Updated description for task.Task 3", TaskStatus.IN_PROGRESS));
        int task4Id = taskManager.createNewTask(new Task("task.Task 4", "Description for task.Task 4"));
        System.out.println("##### TASK UPD #####");
        printAllTasks(taskManager.getTasks());

        int subtask3Id = taskManager.createNewSubtask(new Subtask("task.Subtask #3", "Description for task.Subtask #3", epic2Id));
        int getEpicForUpdateId = taskManager.getEpicById(5).getId();
        ArrayList<Integer> epicForUpdateSubtasksIds = taskManager.getEpicById(getEpicForUpdateId).getSubtasksIds();
        taskManager.updateEpic(new Epic(getEpicForUpdateId, "task.Epic #2 updated", "Updated Description for task.Epic#2", epicForUpdateSubtasksIds));
        System.out.println("##### EPIC UPD #####");
        printAllEpics(taskManager.getEpics());

        int getSubtaskForUpdateId = taskManager.getSubtaskById(9).getId();
        taskManager.updateSubtask(new Subtask(getSubtaskForUpdateId, "task.Subtask #3 updated", "Updated Description for task.Subtask #3", TaskStatus.IN_PROGRESS, epic2Id));
        taskManager.updateSubtask(new Subtask(6, "Test task.Subtask updated", "Updated Description of test subtask", TaskStatus.DONE, testEpicId));
        taskManager.updateSubtask(new Subtask(7, "task.Subtask 2 updated", "Updated Desc for subtask 2", TaskStatus.DONE, testEpicId));
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

//        taskManager.deleteAllTasks();
//        System.out.println("##### DELETE ALL TASKS #####");
//        printAllTasks(taskManager.getTasks());

        System.out.println("##### VIEW HISTORY #####");
        printViewHistory(taskManager.getHistory());

        Task taskIdForHistoryTest = taskManager.getTaskById(1);
        taskIdForHistoryTest = taskManager.getTaskById(3);
        taskIdForHistoryTest = taskManager.getTaskById(8);
        taskIdForHistoryTest = taskManager.getTaskById(3);
        taskIdForHistoryTest = taskManager.getEpicById(5);
        taskIdForHistoryTest = taskManager.getTaskById(3);
        taskIdForHistoryTest = taskManager.getTaskById(1);
        taskIdForHistoryTest = taskManager.getTaskById(1);

        System.out.println("##### VIEW HISTORY #####");
        printViewHistory(taskManager.getHistory());

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

    public static void printViewHistory(ArrayList<Task> history) {
        for (Task task : history) {
            System.out.println(task);
        }
    }
}
