package interpreter.controller;

import interpreter.model.value.IValue;
import interpreter.model.value.RefValue;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class GarbageCollector {
    private GarbageCollector() {}

    public static Set<Integer> getAddrFromSymTable(Collection<IValue> symTableValues) {
        return symTableValues.stream()
                .filter(v -> v instanceof RefValue)
                .map(v -> ((RefValue) v).address())
                .collect(Collectors.toSet());
    }

    public static Map<Integer, IValue> unsafeGarbageCollector(
            Set<Integer> symTableAddr, Map<Integer, IValue> heap) {
        return heap.entrySet().stream()
                .filter(e -> symTableAddr.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Set<Integer> reachableAddresses(Set<Integer> roots, Map<Integer, IValue> heap) {
        Set<Integer> reachable = new HashSet<>(roots);
        boolean changed = true;
        while (changed) {
            Set<Integer> found =
                    reachable.stream()
                            .filter(heap::containsKey)
                            .map(heap::get)
                            .filter(v -> v instanceof RefValue)
                            .map(v -> ((RefValue) v).address())
                            .filter(a -> !reachable.contains(a))
                            .collect(Collectors.toSet());
            changed = reachable.addAll(found);
        }
        return reachable;
    }

    public static Map<Integer, IValue> safeGarbageCollector(
            Set<Integer> symTableAddr, Map<Integer, IValue> heap) {
        Set<Integer> reachable = reachableAddresses(symTableAddr, heap);
        return heap.entrySet().stream()
                .filter(e -> reachable.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
