package calculator;

import java.math.BigDecimal;
import java.util.LinkedList;

public class Calculator {

    private static final String DIVIDE_PROBLEM = "Error: divide by zero.";
    private static final String NUMBER_PROBLEM = "Invalid number type.";
    private static final String CHARTYPE_PROBLEM = "Invalid count of math characters.";

    private String partOfEquation;
    private String value;
    private BigDecimal result;

    public Calculator(String partOfEquation, String value) {
        this.partOfEquation = partOfEquation;
        this.value = value;
    }

    // перевіряємо, чи взагалі результат існує, бо можливо,
    // ми отримаємо значення null, (об'єкт EquationToRPN буде приймати на вхід
    // у конструктор null, або при діленні на 0 буде повертатися null),
    // перехоплюємо його і тоді повертаємо, що розрахунок не можливо провести
    public boolean getCalculable() {
        try {
            return resultOfEquation(editList(
                    new EquationToRPN(partOfEquation).getDigitsAndOperators(), value));
        } catch (Exception e) {
            return false;
        }
    }

    public BigDecimal getResult() {
        return result;
    }

    //замінюємо х на значення, яке ввели, або на рандомне значення, якщо не вводили
    //для того, щоб перевірити потім правильність запису рівняння
    private LinkedList<String> editList(LinkedList<String> list, String variable) {
        int pos = 0;
        while (pos < list.size()) {
            if (list.get(pos).equals("x")) {
                list.set(pos, variable);
            }
            if (pos > 0 && list.get(pos).equals("`")) {
                if (list.get(pos - 1).startsWith("-")) {
                    list.set(pos - 1, list.get(pos - 1).substring(1));
                } else {
                    list.set(pos - 1, "-" + list.get(pos - 1));
                }
                list.remove(pos);
                continue;
            }
            pos++;
        }
        return list;
    }

    //тут логіка розрахунку результату кожної з частин рівняння і також
    //перевіряємо правильність запису послідовності математичних операторів,
    //щоб їх порядок був коректний (не було таких випадків '*/', '+*' тощо)
    //через перевірку вмісту списку - якщо він після всіх
    //операцій більше, ніж 1, то в ньому значить залишилися математичні знаки, отже,
    //рівняння було записано неправильно
    private boolean resultOfEquation(LinkedList<String> list) {
        int pos = 0;
        while (pos < list.size()) {
            if (pos > 1 && list.get(pos).matches("[-+*/]")) {
                if (list.get(pos - 1).matches("-*\\d+.*\\d+|-*\\d+") &&
                        list.get(pos - 2).matches("-*\\d+.*\\d+|-*\\d+")) {
                    char mathChar = list.get(pos).charAt(0);

                    //на всякий випадок ще раз перевіряємо, чи можна привести числа
                    //в строковому вигляді до реальних чисел,
                    //якщо при цьому виникає виключення, то перехоплюємо його
                    try {
                        BigDecimal number1 = new BigDecimal(list.get(pos - 2));
                        BigDecimal number2 = new BigDecimal(list.get(pos - 1));
                        String result = calculate(number1, number2, mathChar);
                        if (result == null) {
                            return false;
                        }
                        list.set(pos - 2, result);
                        list.remove(pos - 1);
                        list.remove(pos - 1);
                        pos = 0;
                        continue;
                    } catch (Exception e) {
                        System.out.println(NUMBER_PROBLEM);
                        return false;
                    }
                }
            }
            pos++;
        }
        if (list.size() != 1) {
            System.out.println(CHARTYPE_PROBLEM);
            return false;
        }
        result = new BigDecimal(list.get(0));
        return true;
    }

    // проводимо математичний обрахунок, використовуючи вміст списку після
    //парсингу вихідної строки в RPN
    //використовуємо округлення до 3 знаку після коми, але можна міняти
    //в залежності від того, що від нас вимагають
    private String calculate(BigDecimal number1, BigDecimal number2, char c) {
        switch (c) {
            case '+':
                return String.valueOf(number1.add(number2).setScale(3, BigDecimal.ROUND_HALF_UP));
            case '-':
                return String.valueOf(number1.subtract(number2).setScale(3, BigDecimal.ROUND_HALF_UP));
            case '*':
                return String.valueOf(number1.multiply(number2).setScale(3, BigDecimal.ROUND_HALF_UP));
            case '/':
                if (number2.compareTo(new BigDecimal("0")) == 0) {
                    System.out.println(DIVIDE_PROBLEM);
                    return null;
                }
                return String.valueOf(number1.divide(number2, BigDecimal.ROUND_HALF_UP).setScale(3, BigDecimal.ROUND_HALF_UP));
        }
        return null;
    }
}

