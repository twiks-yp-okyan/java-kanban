import com.sun.source.tree.NewArrayTree;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        int testTaskId = taskManager.createNewTask(new Task("Test Task", "Description for test task"));
        int anotherTestTaskId = taskManager.createNewTask(new Task("Another test task", "Another description"));
        int task3Id = taskManager.createNewTask(new Task("Task 3", "Description for Task 3"));
        HashMap<Integer, Task> allTasks = taskManager.getTasks();
        for (int taskId : allTasks.keySet()) {
            System.out.println(allTasks.get(taskId));
        }

        /*Epic testEpic = new Epic("Test Epic", "Description for test Epic");
        System.out.println(testEpic);

        Subtask testSubtask = new Subtask("Test Subtask", "Description of test subtask", testEpic.getId());
        System.out.println(testSubtask);

        System.out.println(testEpic);

        Subtask subtask2 = new Subtask("Subtask 2", "Desc for subtask 2", testEpic.getId());
        System.out.println(subtask2);
        System.out.println(testEpic);
        */
    }
}
