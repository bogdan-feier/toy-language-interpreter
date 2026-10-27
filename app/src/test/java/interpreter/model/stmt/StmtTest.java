package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.exp.IExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
        state.getExeStack().pop();
    }

    private static IExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    private static IExp bool(boolean b) {
        return new ValueExp(new BoolValue(b));
    }

    // S1
    @Test
    void compStmtPutsFirstOnTopOfSecond() throws MyException {
        IStmt first = new PrintStmt(num(1));
        IStmt second = new PrintStmt(num(2));
        new CompStmt(first, second).execute(state);
        assertEquals(first, state.getExeStack().pop());
        assertEquals(second, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    // S2
    @Test
    void assignUpdatesTheVariable() throws MyException {
        state.getSymTable().put("v", new IntValue(0));
        new AssignStmt("v", num(2)).execute(state);
        assertEquals(new IntValue(2), state.getSymTable().lookup("v"));
    }

    @Test
    void assignToUndeclaredVariableThrows() {
        assertThrows(StatementException.class, () -> new AssignStmt("v", num(2)).execute(state));
    }

    @Test
    void assignWithTypeMismatchThrows() {
        state.getSymTable().put("v", new IntValue(0));
        assertThrows(StatementException.class, () -> new AssignStmt("v", bool(true)).execute(state));
    }

    // S3
    @Test
    void declaringIntAddsZero() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

    @Test
    void declaringBoolAddsFalse() throws MyException {
        new VarDeclStmt("b", new BoolType()).execute(state);
        assertEquals(new BoolValue(false), state.getSymTable().lookup("b"));
    }

    @Test
    void declaringTheSameVariableTwiceThrows() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        assertThrows(StatementException.class, () -> new VarDeclStmt("v", new IntType()).execute(state));
    }

    // S4
    @Test
    void printAppendsTheValueToOut() throws MyException {
        state.getSymTable().put("v", new IntValue(5));
        new PrintStmt(new VarExp("v")).execute(state);
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(5), state.getOut().get(0));
    }

    @Test
    void printOfUndefinedVariableThrows() {
        assertThrows(ExpressionException.class, () -> new PrintStmt(new VarExp("x")).execute(state));
    }

    // S5
    @Test
    void ifWithTrueConditionPushesThenBranch() throws MyException {
        IStmt thenS = new PrintStmt(num(1));
        IStmt elseS = new PrintStmt(num(2));
        new IfStmt(bool(true), thenS, elseS).execute(state);
        assertEquals(thenS, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void ifWithFalseConditionPushesElseBranch() throws MyException {
        IStmt thenS = new PrintStmt(num(1));
        IStmt elseS = new PrintStmt(num(2));
        new IfStmt(bool(false), thenS, elseS).execute(state);
        assertEquals(elseS, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void ifWithNonBooleanConditionThrows() {
        assertThrows(StatementException.class, () -> new IfStmt(num(1), new NopStmt(), new NopStmt()).execute(state));
    }

    // S6
    @Test
    void nopChangesNothing() throws MyException {
        new NopStmt().execute(state);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(0, state.getOut().size());
    }
}