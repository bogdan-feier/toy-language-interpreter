package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MyStackTest {
    private IStack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new MyStack<>();
    }

    @Test
    void popReturnsLastPushedFirst() throws ADTException {
        stack.push(1);
        stack.push(2);
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }

    @Test
    void popOnEmptyStackThrows() {
        assertThrows(ADTException.class, () -> stack.pop());
    }

    @Test
    void isEmptyReflectsContent() {
        assertTrue(stack.isEmpty());
        stack.push(1);
        assertFalse(stack.isEmpty());
    }

    @Test
    void toListHasTheTopFirst() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(List.of(3, 2, 1), stack.toList());
    }

    @Test
    void toListOfEmptyStackIsEmpty() {
        assertTrue(stack.toList().isEmpty());
    }

    @Test
    void changingTheToListResultDoesNotChangeTheStack() {
        stack.push(1);
        stack.toList().clear();
        assertFalse(stack.isEmpty());
    }
}
