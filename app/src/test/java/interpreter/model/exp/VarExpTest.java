package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VarExpTest {
    private IDictionary<String, IValue> symTable;

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
    }

    @Test
    void returnsTheValueOfADefinedVariable() throws MyException {
        symTable.put("a", new IntValue(2));
        assertEquals(new IntValue(2), new VarExp("a").eval(symTable));
    }

    @Test
    void undefinedVariableThrows() {
        var ex = assertThrows(ExpressionException.class, () -> new VarExp("x").eval(symTable));
        assertEquals("variable x is not defined", ex.getMessage());
    }

    @Test
    void valueExpReturnsItsConstant() throws MyException {
        assertEquals(new IntValue(7), new ValueExp(new IntValue(7)).eval(symTable));
    }
}