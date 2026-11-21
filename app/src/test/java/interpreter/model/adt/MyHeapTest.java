package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import interpreter.model.value.IntValue;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MyHeapTest {
    @Test
    void allocateReturnsAddressesOneTwoThree() {
        IHeap heap = new MyHeap();
        assertEquals(1, heap.allocate(new IntValue(10)));
        assertEquals(2, heap.allocate(new IntValue(20)));
        assertEquals(3, heap.allocate(new IntValue(30)));
    }

    @Test
    void getReturnsTheStoredValue() throws ADTException {
        IHeap heap = new MyHeap();
        int address = heap.allocate(new IntValue(10));
        assertEquals(new IntValue(10), heap.get(address));
    }

    @Test
    void getOfAMissingAddressThrows() {
        assertThrows(ADTException.class, () -> new MyHeap().get(1));
    }

    @Test
    void updateChangesTheValue() throws ADTException {
        IHeap heap = new MyHeap();
        int address = heap.allocate(new IntValue(10));
        heap.update(address, new IntValue(99));
        assertEquals(new IntValue(99), heap.get(address));
    }

    @Test
    void updateOfAMissingAddressThrows() {
        assertThrows(ADTException.class, () -> new MyHeap().update(5, new IntValue(1)));
    }

    @Test
    void isDefinedIsTrueOnlyForAllocatedAddresses() {
        IHeap heap = new MyHeap();
        int address = heap.allocate(new IntValue(10));
        assertTrue(heap.isDefined(address));
        assertFalse(heap.isDefined(0));
        assertFalse(heap.isDefined(address + 1));
    }

    @Test
    void setContentReplacesTheContentButAddressesAreNotReused() {
        IHeap heap = new MyHeap();
        heap.allocate(new IntValue(10));
        heap.allocate(new IntValue(20));
        heap.setContent(Map.of(2, new IntValue(20)));
        assertFalse(heap.isDefined(1));
        assertEquals(3, heap.allocate(new IntValue(30)));
    }
}