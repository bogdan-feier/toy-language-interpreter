package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArithExpTest {
    private IDictionary<String, IValue> symTable;

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
    }

    private static IExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    private static IExp bool(boolean b) {
        return new ValueExp(new BoolValue(b));
    }

    @Test
    void addition() throws MyException {
        assertEquals(new IntValue(5), new ArithExp(ArithOp.ADD, num(2), num(3)).eval(symTable));
    }

    @Test
    void subtraction() throws MyException {
        assertEquals(new IntValue(-1), new ArithExp(ArithOp.SUB, num(2), num(3)).eval(symTable));
    }

    @Test
    void multiplication() throws MyException {
        assertEquals(new IntValue(6), new ArithExp(ArithOp.MUL, num(2), num(3)).eval(symTable));
    }

    @Test
    void division() throws MyException {
        assertEquals(new IntValue(3), new ArithExp(ArithOp.DIV, num(7), num(2)).eval(symTable));
    }

    @Test
    void respectsOperatorPrecedenceEncodedInTheTree() throws MyException {
        IExp e = new ArithExp(ArithOp.ADD, num(2),
                new ArithExp(ArithOp.MUL, num(3), num(5)));
        assertEquals(new IntValue(17), e.eval(symTable));
    }

    @Test
    void divisionByZeroThrows() {
        IExp e = new ArithExp(ArithOp.DIV, num(1), num(0));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("division by zero", ex.getMessage());
    }

    @Test
    void nonIntegerFirstOperandThrows() {
        IExp e = new ArithExp(ArithOp.ADD, bool(true), num(1));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("first operand is not an integer", ex.getMessage());
    }

    @Test
    void nonIntegerSecondOperandThrows() {
        IExp e = new ArithExp(ArithOp.ADD, num(1), bool(true));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("second operand is not an integer", ex.getMessage());
    }
}