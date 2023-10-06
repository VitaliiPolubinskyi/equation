package calculator;

public class CharType {

    private char character;

    public CharType() {}

    public CharType (char character) {
        this.character = character;
    }

    public int getPriority() {
        switch (character) {
            case ')':
                return 0;
            case '(':
                return 1;
            case '+':
            case '-':
                return 2;
            case '*':
            case '/':
                return 3;
            case '`':
                return 4;
        }
        return -1;
    }
}
