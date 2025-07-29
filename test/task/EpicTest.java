package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void shouldEqualsWhenSameIds() {
        Epic epic1 = new Epic(1, "Epic 1", "Desc for Epic1 ", new ArrayList<>());
        Epic epic2 = new Epic(1, "Another Epic 1", "Desc for another epic 1", new ArrayList<>());

        assertEquals(epic1,  epic2);
    }

}