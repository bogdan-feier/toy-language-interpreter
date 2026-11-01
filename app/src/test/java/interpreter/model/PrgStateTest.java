package interpreter.model;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import org.junit.jupiter.api.Test;

class PrgStateTest {
    @Test
    void ofPutsTheProgramOnTheStackAndStartsWithEmptyStructures() throws ADTException {
        IStmt program = new NopStmt();
        PrgState state = PrgState.of(program);

        assertEquals(program, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
        assertTrue(state.getSymTable().toMap().isEmpty());
        assertEquals(0, state.getOut().size());
        assertNotNull(state.getFileTable());
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void toStringShowsTheFileTable() {
        assertTrue(PrgState.of(new NopStmt()).toString().contains("FileTable:"));
    }
}
