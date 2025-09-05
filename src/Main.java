import exceptions.ManagerSaveException;
import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ManagerSaveException {

        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();
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

        System.out.println("##### VIEW HISTORY 1 #####");
        printViewHistory(taskManager.getHistory());


        taskManager.updateTask(new Task(taskIdForUpdateId, "Updated task.Task 3", "Updated description for task.Task 3", TaskStatus.IN_PROGRESS));
        int task4Id = taskManager.createNewTask(new Task("task.Task 4", "Description for task.Task 4"));
//        System.out.println("##### TASK UPD #####");
//        printAllTasks(taskManager.getTasks());

        int subtask3Id = taskManager.createNewSubtask(new Subtask("task.Subtask #3", "Description for task.Subtask #3", epic2Id));
        int getEpicForUpdateId = taskManager.getEpicById(5).getId();
        ArrayList<Integer> epicForUpdateSubtasksIds = taskManager.getEpicById(getEpicForUpdateId).getSubtasksIds();
        taskManager.updateEpic(new Epic(getEpicForUpdateId, "task.Epic #2 updated", "Updated Description for task.Epic#2", epicForUpdateSubtasksIds));
//        System.out.println("##### EPIC UPD #####");
//        printAllEpics(taskManager.getEpics());

        int getSubtaskForUpdateId = taskManager.getSubtaskById(9).getId();
        taskManager.updateSubtask(new Subtask(getSubtaskForUpdateId, "task.Subtask #3 updated", "Updated Description for task.Subtask #3", TaskStatus.IN_PROGRESS, epic2Id));
        taskManager.updateSubtask(new Subtask(6, "Test task.Subtask updated", "Updated Description of test subtask", TaskStatus.DONE, testEpicId));
        taskManager.updateSubtask(new Subtask(7, "task.Subtask 2 updated", "Updated Desc for subtask 2", TaskStatus.DONE, testEpicId));
//        System.out.println("##### SUBTASK UPD #####");
//        printAllSubtasks(taskManager.getSubtasks());
//        printAllEpics(taskManager.getEpics());

        // getAllEpicSubtasks
        ArrayList<Subtask> epicSubtasks = taskManager.getEpicSubtasks(4);
//        System.out.println("-----epic subtasks-----");
//        for (Subtask epicSubtask : epicSubtasks) {
//            System.out.println(epicSubtask);
//        }
        System.out.println("##### UPDATE & CREATE NEW ITEMS #####");
        printAllTasks(taskManager.getTasks());
        printAllEpics(taskManager.getEpics());
        printAllSubtasks(taskManager.getSubtasks());

        System.out.println("##### VIEW HISTORY 2 #####");
        printViewHistory(taskManager.getHistory());

        Task taskForHistoryTest = taskManager.getSubtaskById(6);
        taskForHistoryTest = taskManager.getSubtaskById(7);
        taskForHistoryTest = taskManager.getSubtaskById(7);
        taskForHistoryTest = taskManager.getSubtaskById(6);
        taskForHistoryTest = taskManager.getSubtaskById(9);

        taskForHistoryTest = taskManager.getTaskById(1);
        taskForHistoryTest = taskManager.getTaskById(3);
        taskForHistoryTest = taskManager.getTaskById(8);
        taskForHistoryTest = taskManager.getTaskById(3);
        taskForHistoryTest = taskManager.getTaskById(2);
        taskForHistoryTest = taskManager.getTaskById(1);
        taskForHistoryTest = taskManager.getTaskById(7);
        taskForHistoryTest = taskManager.getTaskById(1);
        taskManager.updateTask(new Task(1, "WWWWWWW", "ASSSSSSSS"));
        taskForHistoryTest = taskManager.getTaskById(1);

        taskForHistoryTest = taskManager.getEpicById(5);
        taskForHistoryTest = taskManager.getEpicById(5);
        taskForHistoryTest = taskManager.getEpicById(5);

        System.out.println("##### VIEW HISTORY 3 #####");
        printViewHistory(taskManager.getHistory());

        System.out.println("##### ITEMS BEFORE DELETE #####");
        printAllTasks(taskManager.getTasks());
        printAllEpics(taskManager.getEpics());
        printAllSubtasks(taskManager.getSubtasks());

        // delete tests
        taskManager.deleteTaskById(2);
//        System.out.println("##### TASK DELETE #####");
//        printAllTasks(taskManager.getTasks());

        taskManager.deleteSubtaskById(7);
        taskManager.deleteSubtaskById(9);
//        System.out.println("##### SUBTASK DELETE #####");
//        printAllSubtasks(taskManager.getSubtasks());
//        printAllEpics(taskManager.getEpics());

        taskManager.deleteEpicById(4);
//        System.out.println("##### DELETE TESTS #####");
//        printAllTasks(taskManager.getTasks());
//        printAllEpics(taskManager.getEpics());
//        printAllSubtasks(taskManager.getSubtasks());

//        taskManager.deleteAllTasks();
//        System.out.println("##### DELETE ALL TASKS #####");
//        printAllTasks(taskManager.getTasks());
        System.out.println("##### ITEMS AFTER DELETE #####");
        printAllTasks(taskManager.getTasks());
        printAllEpics(taskManager.getEpics());
        printAllSubtasks(taskManager.getSubtasks());

//        System.out.println("#####  #####");
//        System.out.println(taskManager.getTaskById(1));
//        Task brandNewTask = new Task(1, "Brand New Task", "Desc", TaskStatus.NEW);
//        System.out.println(brandNewTask);
//        int brandNewTaskId = taskManager.createNewTask(brandNewTask);
//        System.out.println(taskManager.getTaskById(1));

//        printAllTasks(taskManager.getTasks());

//        System.out.println("#####  #####");
//        System.out.println(brandNewTask);

        System.out.println("##### VIEW HISTORY 4 #####");
        printViewHistory(taskManager.getHistory());

        FileBackedTaskManager fileTaskManager = new FileBackedTaskManager("tasks.csv");
        int id1 = fileTaskManager.createNewTask(new Task("Test Task", "Description for test task"));
        int id2 = fileTaskManager.createNewTask(new Task("Another test task", "Another description"));
        int id3 = fileTaskManager.createNewTask(new Task("Task 3", "Description for Task 3"));

        int id4 = fileTaskManager.createNewEpic(new Epic("Test Epic", "Description for test Epic"));
        int id5 = fileTaskManager.createNewEpic(new Epic("Epic #2", "Description for Epic#2"));

        Integer id6 = fileTaskManager.createNewSubtask(new Subtask("Test Subtask", "Description of test subtask", id4));
        Integer id7 = fileTaskManager.createNewSubtask(new Subtask("Subtask 2", "Desc for subtask 2", id5));
        Integer id8 = fileTaskManager.createNewSubtask(new Subtask("Subtask #3", "Description for Subtask #3", id5));

        fileTaskManager.updateSubtask(new Subtask(id8, "Subtask #3", "Description for Subtask #3", TaskStatus.IN_PROGRESS, id5));

        fileTaskManager.deleteTaskById(2);
        fileTaskManager.deleteSubtaskById(7);

        System.out.println("--------");
        FileBackedTaskManager anotherFileTaskManager = FileBackedTaskManager.loadFromFile(new File("tasks.csv"));
        printAllTasks(anotherFileTaskManager.getTasks());
        printAllEpics(anotherFileTaskManager.getEpics());
        printAllSubtasks(anotherFileTaskManager.getSubtasks());
        System.out.println("--------HISTORY");
        printViewHistory(anotherFileTaskManager.getHistory());
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

    public static void printViewHistory(List<Task> history) {
        for (Task task : history) {
            System.out.println(task);
        }
    }
}
