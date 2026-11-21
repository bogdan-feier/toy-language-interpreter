package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.IHeap;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyHeap;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReadHeapExpTest {
    private IDictionary<String, IValue> symTable;
    private IHeap heap;

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
        heap = new MyHeap();
    }

    @Test
    void readsTheValueStoredAtTheAddress() throws MyException {
        int address = heap.allocate(new IntValue(20));
        symTable.put("v", new RefValue(address, new IntType()));
        assertEquals(new IntValue(20), new ReadHeapExp(new VarExp("v")).eval(symTable, heap));
    }

    @Test
    void nestedReadFollowsTwoReferences() throws MyException {
        heap.allocate(new IntValue(20));
        heap.allocate(new RefValue(1, new IntType()));
        symTable.put("a", new RefValue(2, new RefType(new IntType())));
        var exp = new ReadHeapExp(new ReadHeapExp(new VarExp("a")));
        assertEquals(new IntValue(20), exp.eval(symTable, heap));
    }

    @Test
    void anExpressionThatIsNotAReferenceThrows() {
        var exp = new ReadHeapExp(new ValueExp(new IntValue(5)));
        assertThrows(ExpressionException.class, () -> exp.eval(symTable, heap));
    }

    @Test
    void anAddressThatIsNotInTheHeapThrows() {
        symTable.put("v", new RefValue(7, new IntType()));
        var exp = new ReadHeapExp(new VarExp("v"));
        var ex = assertThrows(ExpressionException.class, () -> exp.eval(symTable, heap));
        assertEquals("rH: address 7 is not in the heap", ex.getMessage());
    }

    @Test
    void theNullAddressThrows() {
        symTable.put("v", new RefType(new IntType()).defaultValue()); // (0, int)
        var exp = new ReadHeapExp(new VarExp("v"));
        assertThrows(ExpressionException.class, () -> exp.eval(symTable, heap));
    }
}
