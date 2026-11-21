package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.controller.Controller;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ArithExp;
import interpreter.model.exp.ArithOp;
import interpreter.model.exp.RelOp;
import interpreter.model.exp.RelationalExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WhileStmtTest {
    @TempDir Path dir;

    private static ValueExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    private static ValueExp bool(boolean b) {
        return new ValueExp(new BoolValue(b));
    }

    // int v; v = n; while (v > 0) (print(v); v = v - 1)
    private static IStmt countdown(int n) {
        var v = new VarExp("v");
        var condition = new RelationalExp(RelOp.GT, v, num(0));
        var decrement = new AssignStmt("v", new ArithExp(ArithOp.SUB, v, num(1)));
        var body = new CompStmt(new PrintStmt(v), decrement);
        return new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(new AssignStmt("v", num(n)), new WhileStmt(condition, body)));
    }

    private PrgState run(IStmt program) throws MyException {
        PrgState state = PrgState.of(program);
        new Controller(new Repository(state, dir.resolve("log.txt").toString()), false).allStep();
        return state;
    }

    private static PrgState emptyState() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
        return state;
    }

    @Test
    void trueConditionPushesTheLoopAndThenTheBody() throws MyException {
        PrgState state = emptyState();
        IStmt body = new PrintStmt(num(1));
        var loop = new WhileStmt(bool(true), body);
        loop.execute(state);
        assertEquals(body, state.getExeStack().pop());
        assertEquals(loop, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void falseConditionPushesNothing() throws MyException {
        PrgState state = emptyState();
        new WhileStmt(bool(false), new PrintStmt(num(1))).execute(state);
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void nonBooleanConditionThrows() throws MyException {
        PrgState state = emptyState();
        var loop = new WhileStmt(num(1), new NopStmt());
        assertThrows(StatementException.class, () -> loop.execute(state));
    }

    @Test
    void loopThatRunsZeroTimesPrintsNothing() throws MyException {
        PrgState state = run(countdown(0));
        assertEquals(0, state.getOut().size());
    }

    @Test
    void loopThatRunsOnceHasOneIteration() throws MyException {
        PrgState state = run(countdown(1));
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(1), state.getOut().get(0));
    }

    @Test
    void loopThatRunsSeveralTimesCountsDownToZero() throws MyException {
        PrgState state = run(countdown(3));
        assertEquals(3, state.getOut().size());
        assertEquals(new IntValue(3), state.getOut().get(0));
        assertEquals(new IntValue(2), state.getOut().get(1));
        assertEquals(new IntValue(1), state.getOut().get(2));
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }
}
