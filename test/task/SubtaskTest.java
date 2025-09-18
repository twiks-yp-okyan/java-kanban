package task;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void shouldEqualsWhenSameIds() {
        Subtask subtask1 = new Subtask(1, "Subtask 1", "Desc for subtask 1", LocalDateTime.now(), Duration.ofMinutes(30), TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask(1, "Subtask 2", "Desc for subtask 2", LocalDateTime.now(), Duration.ofMinutes(30), TaskStatus.IN_PROGRESS, 2);

        assertEquals(subtask1, subtask2);
    }

}