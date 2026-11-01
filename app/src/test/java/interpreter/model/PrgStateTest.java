package interpreter.model;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import java.io.BufferedReader;
import java.io.StringReader;
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

    @Test
    void toLogStringHasTheFourSectionsTopOfStackFirst() {
        IStmt program = new NopStmt();
        PrgState state = PrgState.of(program);
        state.getSymTable().put("v", new IntValue(5));
        state.getOut().add(new IntValue(7));
        assertEquals(
                "ExeStack:\n" + program + "\nSymTable:\nv --> 5\nOut:\n7\nFileTable:\n",
                state.toLogString());
    }

    @Test
    void toLogStringListsOpenFilesByName() {
        PrgState state = PrgState.of(new NopStmt());
        state.getFileTable()
                .put(new StringValue("test.in"), new BufferedReader(new StringReader("")));
        assertTrue(state.toLogString().endsWith("FileTable:\ntest.in\n"));
    }
}
