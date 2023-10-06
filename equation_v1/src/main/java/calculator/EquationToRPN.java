package calculator;

import java.util.LinkedList;
import java.util.Stack;


public class EquationToRPN {

    private static final String BRACKETS_PROBLEM = "Wrong number of brackets.";
    private static final String NUMBER_OR_VARIABLE_PROBLEM = "Your number or variable was written uncorrectly.";
    private static final String CHARTYPE_PROBLEM = "Unknown char type in the equation.";


    private LinkedList<String> digitsAndOperators;
    private Stack<Character> operators;

    public EquationToRPN(String str) {
        digitsAndOperators = stringToReservePolishNotation(str);
    }

    public LinkedList<String> getDigitsAndOperators() {
        return digitsAndOperators;
    }



    //основний метод парсингу String -> RPN,
    //тут є перевірка на правильність написання дужок
    //та логіка обробки вхідної строки і обробки стеку
    //на виході маємо список у вигляді RPN
    private LinkedList<String> stringToReservePolishNotation(String str) {
        digitsAndOperators = new LinkedList<String>();
        operators = new Stack<Character>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i)) ||
                    str.charAt(i) == '.' || str.charAt(i) == 'x') {
                builder.append(str.charAt(i));
                if (i == str.length() - 1) {
                    boolean isDigitWrittenRight = checkBuilder(builder);
                    if (!isDigitWrittenRight) {
                        return null;
                    }
                }
            } else {
                boolean isDigitWrittenRight = checkBuilder(builder);
                if (!isDigitWrittenRight) {
                    return null;
                }
                int priority = new CharType(str.charAt(i)).getPriority();
                switch (priority) {
                    case -1:
                        System.out.println(CHARTYPE_PROBLEM);
                        return null;
                    case 0:
                        if (operators.empty()) {
                            System.out.println(BRACKETS_PROBLEM);
                            return null;
                        } else {
                            while (!operators.empty() && operators.peek() != '(') {
                                digitsAndOperators.add(String.valueOf(operators.pop()));
                            }
                            if (operators.empty()) {
                                System.out.println(BRACKETS_PROBLEM);
                                return null;
                            }
                            operators.pop();
                        }
                        break;
                    case 1:
                        operators.push(str.charAt(i));
                        break;
                    case 2:
                        if (str.charAt(i) == '-') {
                            if (i == 0 || (!Character.isDigit(str.charAt(i - 1)) &&
                                    str.charAt(i - 1) != 'x' && str.charAt(i - 1) != ')')) {
                                char c = '`';
                                operators.push(c);
                            } else {
                                actionWithStack(str.charAt(i));
                            }
                        } else {
                            actionWithStack(str.charAt(i));
                        }
                        break;
                    case 3:
                        actionWithStack(str.charAt(i));
                        break;
                }
            }
        }
        while (!operators.empty()) {
            if (operators.peek() == '(') {
                System.out.println(BRACKETS_PROBLEM);
                return null;
            }
            digitsAndOperators.add(String.valueOf(operators.pop()));
        }
        return digitsAndOperators;
    }

    //перевірка правильності написання числа чи змінної
    private boolean checkBuilder(StringBuilder builder) {
        if (builder.length() > 0) {
            String str = builder.toString();
            if (!str.matches("-*\\d+.*\\d+|-*\\d+|x")) {
                System.out.println(NUMBER_OR_VARIABLE_PROBLEM);
                return false;
            }
            digitsAndOperators.add(str);
            builder.delete(0, builder.length());
        }
        return true;
    }


    // загальна логіка для обробки стеку
    private void actionWithStack(char c) {
        if (operators.empty()) {
            operators.push(c);
        } else {
            while (!operators.empty() && new CharType(operators.peek()).getPriority() >=
                    new CharType(c).getPriority()) {
                digitsAndOperators.add(String.valueOf(operators.pop()));
            }
            operators.push(c);
        }
    }
}
