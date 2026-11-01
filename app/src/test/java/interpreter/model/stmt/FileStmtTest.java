package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.type.StringType;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileStmtTest {
    @TempDir Path dir;

    private PrgState state;
    private StringValue fileName;
    private IExp file;

    @BeforeEach
    void setUp() throws Exception {
        Path f = dir.resolve("numbers.in");
        Files.writeString(f, "7\n");
        fileName = new StringValue(f.toString());
        file = new ValueExp(fileName);
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("x", new IntType()).execute(state);
    }

    private IExp fileWithContent(String name, String content) throws IOException {
        Path f = dir.resolve(name);
        Files.writeString(f, content);
        return new ValueExp(new StringValue(f.toString()));
    }

    @Test
    void openAddsTheFileToTheFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertTrue(state.getFileTable().isDefined(fileName));
    }

    @Test
    void openWithNonStringExpressionThrows() {
        IExp notAString = new ValueExp(new IntValue(1));
        assertThrows(StatementException.class, () -> new OpenRFileStmt(notAString).execute(state));
    }

    @Test
    void openingTheSameFileTwiceThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new OpenRFileStmt(file).execute(state));
    }

    @Test
    void openingAMissingFileThrows() {
        IExp missing = new ValueExp(new StringValue(dir.resolve("missing.in").toString()));
        assertThrows(StatementException.class, () -> new OpenRFileStmt(missing).execute(state));
    }

    @Test
    void readGivesTheNextNumberThenZeroAtTheEnd() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(7), state.getSymTable().lookup("x"));
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("x"));
    }

    @Test
    void readReadsTheNumbersInOrder() throws Exception {
        IExp two = fileWithContent("two.in", "15\n50\n");
        new OpenRFileStmt(two).execute(state);
        new ReadFileStmt(two, "x").execute(state);
        assertEquals(new IntValue(15), state.getSymTable().lookup("x"));
        new ReadFileStmt(two, "x").execute(state);
        assertEquals(new IntValue(50), state.getSymTable().lookup("x"));
    }

    @Test
    void readIntoUndeclaredVariableThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "y").execute(state));
    }

    @Test
    void readIntoNonIntVariableThrows() throws MyException {
        new VarDeclStmt("s", new StringType()).execute(state);
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "s").execute(state));
    }

    @Test
    void readFromAFileThatIsNotOpenThrows() {
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "x").execute(state));
    }

    @Test
    void readWithNonStringExpressionThrows() {
        IExp notAString = new ValueExp(new IntValue(1));
        assertThrows(
                StatementException.class, () -> new ReadFileStmt(notAString, "x").execute(state));
    }

    @Test
    void readOfANonIntegerLineThrows() throws Exception {
        IExp bad = fileWithContent("bad.in", "abc\n");
        new OpenRFileStmt(bad).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(bad, "x").execute(state));
    }

    @Test
    void closeRemovesTheFileFromTheFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new CloseRFileStmt(file).execute(state);
        assertFalse(state.getFileTable().isDefined(fileName));
    }

    @Test
    void closingAFileThatIsNotOpenThrows() {
        assertThrows(StatementException.class, () -> new CloseRFileStmt(file).execute(state));
    }

    @Test
    void closeWithNonStringExpressionThrows() {
        IExp notAString = new ValueExp(new IntValue(1));
        assertThrows(StatementException.class, () -> new CloseRFileStmt(notAString).execute(state));
    }

    @Test
    void toStringIsInInfixForm() {
        IExp f = new VarExp("f");
        assertEquals("openRFile(f)", new OpenRFileStmt(f).toString());
        assertEquals("readFile(f, x)", new ReadFileStmt(f, "x").toString());
        assertEquals("closeRFile(f)", new CloseRFileStmt(f).toString());
    }
}
