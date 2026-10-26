package interpreter.model.exp;

import interpreter.exceptions.ADTException;
import interpreter.exceptions.ExpressionException;
import interpreter.model.adt.IDictionary;
import interpreter.model.value.IValue;

public record VarExp(String id) implements IExp {
    @Override
    public IValue eval(IDictionary<String, IValue> symTable) throws ExpressionException {
        if (!symTable.isDefined(id)) {
            throw new ExpressionException("variable " + id + " is not defined");
        }
        try {
            return symTable.lookup(id);
        } catch (ADTException e) {
            throw new ExpressionException(e.getMessage());
        }
    }
}
