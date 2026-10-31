package interpreter.model.type;

import interpreter.model.value.StringValue;
import interpreter.model.value.IValue;

public record StringType() implements IType {
    @Override
    public IValue defaultValue() {
        return new StringValue("");
    }

    @Override
    public String toString() {
        return "string";
    }
}
