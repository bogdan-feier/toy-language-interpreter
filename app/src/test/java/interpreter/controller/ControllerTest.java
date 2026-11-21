package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.stmt.CompStmt;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NewStmt;
import interpreter.model.stmt.VarDeclStmt;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ControllerTest {
    @TempDir Path dir;

    private String logPath() {
        return dir.resolve("log.txt").toString();
    }

    private PrgState newState(IStmt program) {
        return PrgState.of(program);
    }

    private PrgState run(IStmt program) throws MyException {
        PrgState state = PrgState.of(program);
        new Controller(new Repository(state, logPath()), false).allStep();
        return state;
    }

    @Test
    void example1PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX1);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(2), state.getOut().get(0));
        assertEquals(new IntValue(2), state.getSymTable().lookup("v"));
    }

    @Test
    void example2PrintsEighteen() throws MyException {
        PrgState state = run(Examples.EX2);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(18), state.getOut().get(0));
    }

    @Test
    void example3PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX3);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void oneStepOnEmptyStackThrows() throws MyException {
        PrgState state = newState(Examples.EX1);
        state.getExeStack().pop();
        Controller controller = new Controller(new Repository(state, logPath()), false);
        assertThrows(StatementException.class, () -> controller.oneStep(state));
    }

    @Test
    void errorInsideAProgramStopsExecutionWithAnException() {
        IStmt declaredTwice =
                new CompStmt(
                        new VarDeclStmt("v", new IntType()), new VarDeclStmt("v", new IntType()));
        PrgState state = newState(declaredTwice);
        Controller controller = new Controller(new Repository(state, logPath()), false);
        assertThrows(StatementException.class, controller::allStep);
    }

    @Test
    void logFileContainsTheFourSectionsInOrder() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        int a = log.indexOf("ExeStack:");
        int b = log.indexOf("SymTable:");
        int c = log.indexOf("Out:");
        int d = log.indexOf("FileTable:");
        assertTrue(0 <= a && a < b && b < c && c < d);
    }

    @Test
    void logFileHasTheInitialStateAndTwoStatesPerStep() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        assertEquals(11, log.split("ExeStack:", -1).length - 1);
        assertTrue(log.contains("Out:\n2\n"));
    }

    @Test
    void fileExampleReadsBothNumbersAndClosesTheFile() throws Exception {
        Path in = dir.resolve("test.in");
        Files.writeString(in, "15\n50\n");
        PrgState s = run(Examples.fileExample(in.toString()));
        assertEquals(new IntValue(15), s.getOut().get(0));
        assertEquals(new IntValue(50), s.getOut().get(1));
        assertTrue(s.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExampleOnAnEmptyFilePrintsZeroTwice() throws Exception {
        Path in = dir.resolve("empty.in");
        Files.writeString(in, "");
        PrgState s = run(Examples.fileExample(in.toString()));
        assertEquals(new IntValue(0), s.getOut().get(0));
        assertEquals(new IntValue(0), s.getOut().get(1));
    }

    @Test
    void example5PrintsFive() throws MyException {
        PrgState s = run(Examples.EX5);
        assertEquals(new IntValue(5), s.getOut().get(0));
    }

    @Test
    void garbageCollectorRemovesALocationNoVariableReachesAnymore() throws MyException {
        // Ref int v; new(v, 20); new(v, 30)
        IStmt program =
                new CompStmt(
                        new VarDeclStmt("v", new RefType(new IntType())),
                        new CompStmt(
                                new NewStmt("v", new ValueExp(new IntValue(20))),
                                new NewStmt("v", new ValueExp(new IntValue(30)))));
        PrgState state = run(program);
        assertEquals(Set.of(2), state.getHeap().getContent().keySet());
        assertEquals(new IntValue(30), state.getHeap().get(2));
    }

    @Test
    void example6LeavesTwoLocationsAndPrintsTwoReferences() throws MyException {
        PrgState s = run(Examples.EX6);
        assertEquals(new RefValue(1, new IntType()), s.getOut().get(0));
        assertEquals(new RefValue(2, new RefType(new IntType())), s.getOut().get(1));
        assertEquals(Set.of(1, 2), s.getHeap().getContent().keySet());
        assertEquals(new IntValue(20), s.getHeap().get(1));
        assertEquals(new RefValue(1, new IntType()), s.getHeap().get(2));
    }

    @Test
    void example7Prints20And25() throws MyException {
        PrgState s = run(Examples.EX7);
        assertEquals(new IntValue(20), s.getOut().get(0));
        assertEquals(new IntValue(25), s.getOut().get(1));
    }

    @Test
    void example8Prints20And35AndChangesTheHeap() throws MyException {
        PrgState s = run(Examples.EX8);
        assertEquals(new IntValue(20), s.getOut().get(0));
        assertEquals(new IntValue(35), s.getOut().get(1));
        assertEquals(Set.of(1), s.getHeap().getContent().keySet());
        assertEquals(new IntValue(30), s.getHeap().get(1));
    }

    @Test
    void example9Prints20BecauseTheSafeCollectorKeepsLocationOne() throws MyException {
        PrgState s = run(Examples.EX9);
        assertEquals(1, s.getOut().size());
        assertEquals(new IntValue(20), s.getOut().get(0));
        assertEquals(Set.of(1, 2, 3), s.getHeap().getContent().keySet());
    }

    @Test
    void example10CountsDownFromFourToZero() throws MyException {
        PrgState s = run(Examples.EX10);
        assertEquals(5, s.getOut().size());
        for (int i = 0; i < 5; i++) {
            assertEquals(new IntValue(4 - i), s.getOut().get(i));
        }
    }
}
