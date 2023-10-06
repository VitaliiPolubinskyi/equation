package database;

public class Equation {
    private long id;
    private String equation;
    private String variable;

    public Equation(String equation) {
        this.equation = equation;
    }

       public Equation(String equation, String variable) {
        this.equation = equation;
        this.variable = variable;
    }

    public Equation(long id, String equation, String variable) {
        this.id = id;
        this.equation = equation;
        this.variable = variable;
    }

    public String getEquation() {
        return equation;
    }

    public long getId() { return id; }

    public String getVariable() {
        return variable;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    @Override
    public String toString() {
        return "Equation{" +
                "equation='" + equation + '\'' +
                ", variable='" + variable + '\'' +
                '}';
    }
}
