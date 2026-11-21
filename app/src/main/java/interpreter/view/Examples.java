package interpreter.view;

import interpreter.model.exp.*;
import interpreter.model.stmt.*;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.type.StringType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;

public class Examples {
    // Ex1: int v; v = 2; print(v)
    public static final IStmt EX1 =
            new CompStmt(
                    new VarDeclStmt("v", new IntType()),
                    new CompStmt(
                            new AssignStmt("v", new ValueExp(new IntValue(2))),
                            new PrintStmt(new VarExp("v"))));

    // Ex2: int a; int b; a = 2 + 3 * 5; b = a + 1; print(b)
    public static final IStmt EX2 =
            new CompStmt(
                    new VarDeclStmt("a", new IntType()),
                    new CompStmt(
                            new VarDeclStmt("b", new IntType()),
                            new CompStmt(
                                    new AssignStmt(
                                            "a",
                                            new ArithExp(
                                                    ArithOp.ADD,
                                                    new ValueExp(new IntValue(2)),
                                                    new ArithExp(
                                                            ArithOp.MUL,
                                                            new ValueExp(new IntValue(3)),
                                                            new ValueExp(new IntValue(5))))),
                                    new CompStmt(
                                            new AssignStmt(
                                                    "b",
                                                    new ArithExp(
                                                            ArithOp.ADD,
                                                            new VarExp("a"),
                                                            new ValueExp(new IntValue(1)))),
                                            new PrintStmt(new VarExp("b"))))));

    // Ex3: bool a; int v; a = true; (if a then v = 2 else v = 3); print(v)
    public static final IStmt EX3 =
            new CompStmt(
                    new VarDeclStmt("a", new BoolType()),
                    new CompStmt(
                            new VarDeclStmt("v", new IntType()),
                            new CompStmt(
                                    new AssignStmt("a", new ValueExp(new BoolValue(true))),
                                    new CompStmt(
                                            new IfStmt(
                                                    new VarExp("a"),
                                                    new AssignStmt(
                                                            "v", new ValueExp(new IntValue(2))),
                                                    new AssignStmt(
                                                            "v", new ValueExp(new IntValue(3)))),
                                            new PrintStmt(new VarExp("v"))))));

    // fileExample: string varf; varf = "<fileName>"; openRFile(varf); int varc;
    //   readFile(varf, varc); print(varc); readFile(varf, varc); print(varc); closeRFile(varf)
    public static IStmt fileExample(String fileName) {
        return new CompStmt(
                new VarDeclStmt("varf", new StringType()),
                new CompStmt(
                        new AssignStmt("varf", new ValueExp(new StringValue(fileName))),
                        new CompStmt(
                                new OpenRFileStmt(new VarExp("varf")),
                                new CompStmt(
                                        new VarDeclStmt("varc", new IntType()),
                                        new CompStmt(
                                                new ReadFileStmt(new VarExp("varf"), "varc"),
                                                new CompStmt(
                                                        new PrintStmt(new VarExp("varc")),
                                                        new CompStmt(
                                                                new ReadFileStmt(
                                                                        new VarExp("varf"), "varc"),
                                                                new CompStmt(
                                                                        new PrintStmt(
                                                                                new VarExp("varc")),
                                                                        new CloseRFileStmt(
                                                                                new VarExp(
                                                                                        "varf"))))))))));
    }

    // Ex4: the file example on test.in
    public static final IStmt EX4 = fileExample("test.in");

    // Ex5: int a; int b; a = 5; b = 7; (if a < b then print(a) else print(b))
    public static final IStmt EX5 =
            new CompStmt(
                    new VarDeclStmt("a", new IntType()),
                    new CompStmt(
                            new VarDeclStmt("b", new IntType()),
                            new CompStmt(
                                    new AssignStmt("a", new ValueExp(new IntValue(5))),
                                    new CompStmt(
                                            new AssignStmt("b", new ValueExp(new IntValue(7))),
                                            new IfStmt(
                                                    new RelationalExp(
                                                            RelOp.LT,
                                                            new VarExp("a"),
                                                            new VarExp("b")),
                                                    new PrintStmt(new VarExp("a")),
                                                    new PrintStmt(new VarExp("b")))))));

    // seq(a, b, c) builds (a; (b; c)), the same tree as CompStmt(a, CompStmt(b, c))
    private static IStmt seq(IStmt... stmts) {
        IStmt result = stmts[stmts.length - 1];
        for (int i = stmts.length - 2; i >= 0; i--) {
            result = new CompStmt(stmts[i], result);
        }
        return result;
    }

    private static IExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    private static IExp variable(String name) {
        return new VarExp(name);
    }

    // Ex6: Ref int v; new(v, 20); Ref Ref int a; new(a, v); print(v); print(a)
    public static final IStmt EX6 =
            seq(
                    new VarDeclStmt("v", new RefType(new IntType())),
                    new NewStmt("v", num(20)),
                    new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                    new NewStmt("a", variable("v")),
                    new PrintStmt(variable("v")),
                    new PrintStmt(variable("a")));

    // Ex7: Ref int v; new(v, 20); Ref Ref int a; new(a, v); print(rH(v)); print(rH(rH(a)) + 5)
    public static final IStmt EX7 =
            seq(
                    new VarDeclStmt("v", new RefType(new IntType())),
                    new NewStmt("v", num(20)),
                    new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                    new NewStmt("a", variable("v")),
                    new PrintStmt(new ReadHeapExp(variable("v"))),
                    new PrintStmt(
                            new ArithExp(
                                    ArithOp.ADD,
                                    new ReadHeapExp(new ReadHeapExp(variable("a"))),
                                    num(5))));

    // Ex8: Ref int v; new(v, 20); print(rH(v)); wH(v, 30); print(rH(v) + 5)
    public static final IStmt EX8 =
            seq(
                    new VarDeclStmt("v", new RefType(new IntType())),
                    new NewStmt("v", num(20)),
                    new PrintStmt(new ReadHeapExp(variable("v"))),
                    new WriteHeapStmt("v", num(30)),
                    new PrintStmt(
                            new ArithExp(ArithOp.ADD, new ReadHeapExp(variable("v")), num(5))));

    // Ex9: Ref int v; new(v, 20); Ref Ref int a; new(a, v); new(v, 30); print(rH(rH(a)))
    public static final IStmt EX9 =
            seq(
                    new VarDeclStmt("v", new RefType(new IntType())),
                    new NewStmt("v", num(20)),
                    new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                    new NewStmt("a", variable("v")),
                    new NewStmt("v", num(30)),
                    new PrintStmt(new ReadHeapExp(new ReadHeapExp(variable("a")))));

    // Ex10: int v; v = 4; (while (v > 0) (print(v); v = v - 1)); print(v)
    public static final IStmt EX10 =
            seq(
                    new VarDeclStmt("v", new IntType()),
                    new AssignStmt("v", num(4)),
                    new WhileStmt(
                            new RelationalExp(RelOp.GT, variable("v"), num(0)),
                            seq(
                                    new PrintStmt(variable("v")),
                                    new AssignStmt(
                                            "v",
                                            new ArithExp(ArithOp.SUB, variable("v"), num(1))))),
                    new PrintStmt(variable("v")));
}
