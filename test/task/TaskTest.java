package task;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldEqualsWhenSameIds() {
        Task task1 = new Task(1, "Task 1", "Desc for Task1 ", TaskStatus.NEW);
        Task task2 = new Task(1, "Another Task 1", "Desc for another task 1", TaskStatus.DONE);

        assertEquals(task1,  task2);
    }
  
}