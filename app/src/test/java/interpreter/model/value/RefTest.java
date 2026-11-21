package interpreter.model.value;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import org.junit.jupiter.api.Test;

class RefTest {
    @Test
    void refDefaultValueIsAddressZeroWithTheInnerType() {
        assertEquals(new RefValue(0, new IntType()), new RefType(new IntType()).defaultValue());
    }

    @Test
    void nestedRefTypesAreComparedRecursively() {
        assertEquals(
                new RefType(new RefType(new IntType())), new RefType(new RefType(new IntType())));
    }

    @Test
    void refOfRefDiffersFromRef() {
        assertNotEquals(new RefType(new RefType(new IntType())), new RefType(new IntType()));
    }

    @Test
    void refValueHasRefTypeOfItsLocationType() {
        assertEquals(new RefType(new IntType()), new RefValue(1, new IntType()).getType());
    }

    @Test
    void refValueToStringShowsAddressAndLocationType() {
        assertEquals("(2, Ref(int))", new RefValue(2, new RefType(new IntType())).toString());
    }
}
