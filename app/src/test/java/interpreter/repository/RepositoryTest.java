package interpreter.repository;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.RepositoryException;
import interpreter.model.PrgState;
import interpreter.model.stmt.NopStmt;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RepositoryTest {
    @TempDir Path dir;

    @Test
    void everyCallAppendsAnotherStateToTheLogFile() throws Exception {
        Path log = dir.resolve("log.txt");
        var repo = new Repository(PrgState.of(new NopStmt()), log.toString());
        repo.logPrgStateExec();
        repo.logPrgStateExec();
        assertEquals(2, Files.readString(log).split("ExeStack:", -1).length - 1);
    }

    @Test
    void anUnwritableLogFileThrowsARepositoryException() {
        String bad = dir.resolve("no-such-folder/log.txt").toString();
        var repo = new Repository(PrgState.of(new NopStmt()), bad);
        assertThrows(RepositoryException.class, repo::logPrgStateExec);
    }
}
