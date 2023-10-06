package main;

import database.*;
import calculator.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {

        //створюємо бд, якщо помилка - завершуємо програму
        boolean dbIsCreated = DBCRUD.createTable();
        if (!dbIsCreated) {
            return;
        }

        //ввід рівняння та можливого розв'яку в циклі, щоб можна було вводити
        //стільки рівнянь, скільки потрібно
        Scanner console = new Scanner(System.in);
        while (true) {
            System.out.println("Please, write down an equation. To exit write down \"exit\".");
            String equation = console.nextLine();
            if (equation.equalsIgnoreCase("exit")) {
                break;
            }
            if (equation.equals("")) {
                continue;
            }

            System.out.println("Do you want to enter a possible solution of the equation? " +
                    "Please, answer \"yes\" or \"no\". ");
            String answer = console.nextLine();
            String solution = "";
            if (answer.equals("yes")) {
                System.out.println("Please, write down a possible solution.");
                solution = console.nextLine();
            } else if (answer.equals("no")) {
                solution = "x";
            } else {
                System.out.println("You wrote an illegal answer. Try again.");
                continue;
            }

            //ділимо рівняння на праву та ліву частини, перевіряємо, чи вони
            //задовільняють шаблону рівняння a=b
            //щоб проводити з ними дії окремо
            String leftPartOfEquation;
            String rightPartOfEquation;
            try {
                int equalLocation = equation.indexOf('=');
                leftPartOfEquation = equation.substring(0, equalLocation);
                rightPartOfEquation = equation.substring(equalLocation + 1);
            } catch (Exception e) {
                System.out.println("Invalid data-in.");
                continue;
            }

            //перевірка правої частини на коректність запису
            //по кількості символів '='
            boolean hasMoreEqualCharInRightPart = checkEqualCharacter(rightPartOfEquation);

            if (hasMoreEqualCharInRightPart) {
                System.out.println("The equation has too many equal characters.");
                continue;
            }

            //основні обчислення та запис у БД

            if (solution.equals("x")) {
                Calculator left = new Calculator(leftPartOfEquation, String.valueOf(Math.random()+1));
                Calculator right = new Calculator(rightPartOfEquation, String.valueOf(Math.random()+1));
                if (left.getCalculable() && right.getCalculable()) {
                    boolean isSaved = DBCRUD.saveEquation(new Equation(equation));
                    if (isSaved) {
                        System.out.println("The equation was saved without answer.");
                    }
                }
            } else {
                Calculator left = new Calculator(leftPartOfEquation, solution);
                Calculator right = new Calculator(rightPartOfEquation, solution);
                if (left.getCalculable() && right.getCalculable()) {
                    BigDecimal leftDigit = left.getResult();
                    BigDecimal rightDigit = right.getResult();
                    if ((leftDigit.subtract(rightDigit).abs())
                            .compareTo(new BigDecimal("1e-9")) <= 0 ) {
                        //код, що пише просто строчку в базу даних як рівняння, а відповідь solution
                        boolean isSaved = DBCRUD.saveEquation(new Equation(equation, solution));
                        if (isSaved) {
                            System.out.println("The equation was saved with answer.");
                        }
                    } else {
                        //код, що пише просто строчку в базу даних як рівняння, а відповідь null
                        boolean isSaved = DBCRUD.saveEquation(new Equation(equation));
                        if (isSaved) {
                            System.out.println("Wrong value of written variable. " +
                                    "The equation was saved without answer.");
                        }
                    }
                }
            }
        }

        //перевіряємо, чи наша БД містить якісь дані
        if (DBCRUD.getListOfEquations() == null) {
            System.out.println("The list of data is empty. Program is closing.");
            return;
        }


        //обробка даних в БД


        while (true) {
            System.out.println("Do you want to analyze data? " +
                    "Type:\n\"1\" if no\n" +
                    "\"2\" if you want to look at all data\n" +
                    "\"3\" if you want to look at data grouped by variable value\n" +
                    "\"4\" if you want to look at data grouped by number of possible answers");

            String answer = console.nextLine();
            if (answer.equals("1")) {
                console.close();
                return;
            }
            if (answer.equals("2")) {
                List<Equation> list = DBCRUD.getListOfEquations();
                System.out.println(list);
            }
            if (answer.equals("3")) {
                System.out.println("Enter variable value.");
                String variable = console.nextLine();
                if (variable.equals("")) {
                    List<Equation> list = DBCRUD.getListOfSameVariableValue("SELECT equation, variable FROM task1 WHERE variable IS NULL");
                    System.out.println(list);
                    continue;
                } else {
                    List<Equation> list = DBCRUD.getListOfSameVariableValue("SELECT equation, variable FROM task1 WHERE variable LIKE " + variable);
                    if (list.size() == 0) {
                        System.out.println("The list of data is empty.");
                        continue;
                    }
                    System.out.println(list);
                }
            }
            if (answer.equals("4")) {
                System.out.println("Enter a number of possible answers.");
                String variable = console.nextLine();
                if (variable == null) {
                    List<Equation> list = DBCRUD.getListOfOneVariableValue("SELECT equation FROM task1 WHERE variable IS NULL");
                    if (list == null) {
                        System.out.println("The list of data is empty.");
                        continue;
                    }
                    list.forEach(System.out::println);
                    continue;
                }
                List<Equation> list = DBCRUD.getListOfOneVariableValue("SELECT equation FROM task1 WHERE " +
                        "variable IS NOT NULL GROUP BY equation HAVING COUNT(*) LIKE " + variable);
                if (list.size() == 0) {
                    System.out.println("The list of data is empty.");
                    continue;
                }
                list.forEach(equation -> System.out.println(equation.getEquation()));
            }
        }
    }

    //метод перевірки кількості символів '='
    public static boolean checkEqualCharacter(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '=')
                return true;
        }
        return false;
    }
}

