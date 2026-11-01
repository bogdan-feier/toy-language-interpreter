package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import java.util.List;

public interface IList<T> {
    void add(T value);

    T get(int index) throws ADTException;

    int size();

    List<T> toList();
}
