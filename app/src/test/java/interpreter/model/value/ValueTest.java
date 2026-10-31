package interpreter.model.value;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.type.StringType;
import org.junit.jupiter.api.Test;

class ValueTest {
    @Test
    void intValueHasIntType() {
        assertEquals(new IntType(), new IntValue(5).getType());
    }

    @Test
    void boolValueHasBoolType() {
        assertEquals(new BoolType(), new BoolValue(true).getType());
    }

    @Test
    void intDefaultValueIsZero() {
        assertEquals(new IntValue(0), new IntType().defaultValue());
    }

    @Test
    void boolDefaultValueIsFalse() {
        assertEquals(new BoolValue(false), new BoolType().defaultValue());
    }

    @Test
    void differentTypesAreNotEqual() {
        assertNotEquals(new IntType(), new BoolType());
    }

    @Test
    void stringValueHasStringType() {
        assertEquals(new StringType(), new StringValue("abc").getType());
    }

    @Test
    void stringDefaultValueIsEmptyString() {
        assertEquals(new StringValue(""), new StringType().defaultValue());
    }

    @Test
    void equalStringValuesAreEqual() {
        assertEquals(new StringValue("test.in"), new StringValue("test.in"));
    }

    @Test
    void equalStringValuesHaveTheSameHashCode() {
        assertEquals(new StringValue("test.in").hashCode(), new StringValue("test.in").hashCode());
    }

    @Test
    void differentStringValuesAreNotEqual() {
        assertNotEquals(new StringValue("a"), new StringValue("b"));
    }
}
