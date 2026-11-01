package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MyDictionaryTest {
    private IDictionary<String, Integer> dict;

    @BeforeEach
    void setUp() {
        dict = new MyDictionary<>();
    }

    @Test
    void putThenLookupReturnsValue() throws ADTException {
        dict.put("a", 1);
        assertTrue(dict.isDefined("a"));
        assertEquals(1, dict.lookup("a"));
    }

    @Test
    void updateChangesValue() throws ADTException {
        dict.put("a", 1);
        dict.update("a", 5);
        assertEquals(5, dict.lookup("a"));
    }

    @Test
    void updateOfMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.update("x", 1));
    }

    @Test
    void removeDeletesTheKey() throws ADTException {
        dict.put("a", 1);
        dict.remove("a");
        assertFalse(dict.isDefined("a"));
    }

    @Test
    void removeOfMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.remove("x"));
    }

    @Test
    void toMapContainsTheEntries() {
        dict.put("a", 1);
        dict.put("b", 2);
        assertEquals(Map.of("a", 1, "b", 2), dict.toMap());
    }

    @Test
    void toMapIsReadOnly() {
        dict.put("a", 1);
        Map<String, Integer> copy = dict.toMap();
        assertThrows(UnsupportedOperationException.class, () -> copy.put("b", 2));
    }

    @Test
    void toMapIsACopyThatDoesNotFollowLaterChanges() {
        dict.put("a", 1);
        Map<String, Integer> copy = dict.toMap();
        dict.put("b", 2);
        assertEquals(Map.of("a", 1), copy);
    }
}
