package interpreter.view;

import interpreter.controller.Controller;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.repository.Repository;
import java.util.List;
import java.util.Scanner;

public class TextMenu {
    private final Scanner in = new Scanner(System.in);
    private final List<IStmt> programs = List.of(Examples.EX1, Examples.EX2, Examples.EX3);
    private IStmt selected = null;

    public void run() {
        while (true) {
            printMenu();
            String choice = readLine();
            if (choice == null) {
                return;
            }
            switch (choice.trim()) {
                case "1" -> selectProgram();
                case "2" -> runProgram();
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            };
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Select a program");
        System.out.println("2. Run the selected program");
        System.out.println("0. Exit");
        System.out.print("Option: ");
    }

    private void selectProgram() {
        for (int i = 0; i < programs.size(); i++) {
            System.out.println((i + 1) + ". " + programs.get(i));
        }
        System.out.print("Program number: ");
        String line = readLine();
        if (line == null) {
            return;
        }
        try {
            int number = Integer.parseInt(line.trim());
            if (number < 1 || number > programs.size()) {
                System.out.println("There is no program " + number + ".");
                return;
            }
            selected = programs.get(number - 1);
            System.out.println("Selected: " + selected);
        } catch (NumberFormatException e) {
            System.out.println("Please type a number.");
        }
    }

    private void runProgram() {
        if (selected == null) {
            System.out.println("Select a program first.");
            return;
        }
        PrgState state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), selected);
        Controller controller = new Controller(new Repository(state), true);
        try {
            controller.allStep();
        } catch (MyException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String readLine() {
        return in.hasNextLine() ? in.nextLine() : null;
    }
}
