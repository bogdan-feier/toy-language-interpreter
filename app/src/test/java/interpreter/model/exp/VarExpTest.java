package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.IHeap;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyHeap;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VarExpTest {
    private IDictionary<String, IValue> symTable;
    private IHeap heap;

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
        heap = new MyHeap();
    }

    @Test
    void returnsTheValueOfADefinedVariable() throws MyException {
        symTable.put("a", new IntValue(2));
        assertEquals(new IntValue(2), new VarExp("a").eval(symTable, heap));
    }

    @Test
    void undefinedVariableThrows() {
        var ex =
                assertThrows(ExpressionException.class, () -> new VarExp("x").eval(symTable, heap));
        assertEquals("variable x is not defined", ex.getMessage());
    }

    @Test
    void valueExpReturnsItsConstant() throws MyException {
        assertEquals(new IntValue(7), new ValueExp(new IntValue(7)).eval(symTable, heap));
    }
}
