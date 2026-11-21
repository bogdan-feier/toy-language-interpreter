package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.repository.IRepository;
import java.util.Set;

public class Controller {
    private final IRepository repo;
    private final boolean displayFlag;

    public Controller(IRepository repo, boolean displayFlag) {
        this.repo = repo;
        this.displayFlag = displayFlag;
    }

    public PrgState oneStep(PrgState state) throws MyException {
        var stack = state.getExeStack();
        if (stack.isEmpty()) {
            throw new StatementException("execution stack is empty");
        }
        IStmt crtStmt = stack.pop();
        return crtStmt.execute(state);
    }

    public void allStep() throws MyException {
        PrgState prg = repo.getCrtPrg();
        repo.logPrgStateExec();
        display(prg);
        while (!prg.getExeStack().isEmpty()) {
            oneStep(prg);
            repo.logPrgStateExec();
            collectGarbage(prg);
            repo.logPrgStateExec();
            display(prg);
        }
    }

    private void collectGarbage(PrgState prg) {
        var heap = prg.getHeap();
        Set<Integer> roots =
                GarbageCollector.getAddrFromSymTable(prg.getSymTable().toMap().values());
        // heap.setContent(GarbageCollector.unsafeGarbageCollector(roots, heap.getContent()));
        heap.setContent(GarbageCollector.safeGarbageCollector(roots, heap.getContent()));
    }

    private void display(PrgState prg) {
        if (displayFlag) {
            System.out.println(prg);
        }
    }
}
