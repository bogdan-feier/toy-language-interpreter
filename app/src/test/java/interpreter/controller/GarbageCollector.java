package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GarbageCollectorTest {
    // the state of Ex9 after new(v, 30): v -> (3, int), a -> (2, Ref(int))
    private final List<IValue> symTable =
            List.of(new RefValue(3, new IntType()), new RefValue(2, new RefType(new IntType())));
    private final Map<Integer, IValue> heap =
            Map.of(
                    1, new IntValue(20),
                    2, new RefValue(1, new IntType()),
                    3, new IntValue(30),
                    4, new IntValue(7));

    @Test
    void getAddrFromSymTableTakesOnlyTheReferences() {
        List<IValue> values = List.of(new IntValue(5), new RefValue(3, new IntType()));
        assertEquals(Set.of(3), GarbageCollector.getAddrFromSymTable(values));
    }

    @Test
    void unsafeCollectorRemovesALocationThatIsReachableOnlyThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);
        Map<Integer, IValue> kept = GarbageCollector.unsafeGarbageCollector(roots, heap);
        // 4 is real garbage, but 1 is lost too: a still reaches it through location 2
        assertEquals(Set.of(2, 3), kept.keySet());
    }
}
