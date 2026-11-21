package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;

public record WhileStmt(IExp cond, IStmt body) implements IStmt {
    @Override
    public PrgState execute(PrgState state) throws MyException {
        IValue c = cond.eval(state.getSymTable(), state.getHeap());
        if (!(c instanceof BoolValue(boolean b))) {
            throw new StatementException("condition " + cond + " is not a boolean");
        }
        if (b) {
            state.getExeStack().push(this); // the loop comes back after the body
            state.getExeStack().push(body); // the body runs first
        }
        return state;
    }

    @Override
    public String toString() {
        return "(while (" + cond + ") " + body + ")";
    }
}
