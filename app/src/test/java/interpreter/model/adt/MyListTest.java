package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MyListTest {
    private IList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new MyList<>();
    }

    @Test
    void addIncreasesSizeAndKeepsOrder() throws ADTException {
        list.add(10);
        list.add(20);
        assertEquals(2, list.size());
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
    }

    @Test
    void getOutOfBoundsThrows() {
        assertThrows(ADTException.class, () -> list.get(0));
    }

    @Test
    void toListKeepsInsertionOrder() {
        list.add(10);
        list.add(20);
        list.add(30);
        assertEquals(List.of(10, 20, 30), list.toList());
    }

    @Test
    void changingTheToListResultDoesNotChangeTheList() {
        list.add(10);
        list.toList().clear();
        assertEquals(1, list.size());
    }
}
