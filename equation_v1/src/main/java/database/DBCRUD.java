package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBCRUD {

    public static boolean createTable() {
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS task1 " +
                    "(id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "equation VARCHAR(1000) NOT NULL, " +
                    "variable VARCHAR(1000) DEFAULT NULL)";
            statement.execute(sql);
            return true;
        } catch (SQLException e) {
            System.out.println("Problem with table creating.");
            return false;
        }

    }

    public static boolean saveEquation(Equation equation) {
        List<Equation> list = getListOfEquations();
        if (list != null) {
            for (Equation eq : list) {
                if (eq.getVariable() != null && equation.getVariable() != null) {
                    if (eq.getEquation().equals(equation.getEquation()) &&
                            eq.getVariable().equals(equation.getVariable())) {
                        System.out.println("This equation already exists in the table.");
                        return false;
                    }
                } else if(eq.getVariable() == null && equation.getVariable() == null) {
                    if (eq.getEquation().equals(equation.getEquation())) {
                        System.out.println("This equation already exists in the table.");
                        return false;
                    }
                }
            }
        }

        String insertEquation = "INSERT INTO task1 (equation, variable) " +
                "VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertEquation)) {
            preparedStatement.setString(1, equation.getEquation());
            preparedStatement.setString(2, equation.getVariable());

            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Problem with query.");
            return false;
        }
    }

    public static List<Equation> getListOfEquations() {
        String query = "SELECT * FROM task1";
        List<Equation> equations = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String equation = resultSet.getString("equation");
                String variable = resultSet.getString("variable");

                equations.add(new Equation(id, equation, variable));
            }
            return equations;
        } catch (SQLException e) {
            System.out.println("Problem with query.");
            return null;
        }
    }

    public static List<Equation> getListOfSameVariableValue(String query) {
        List<Equation> equations = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String equation = resultSet.getString("equation");
                String variable = resultSet.getString("variable");

                equations.add(new Equation(equation, variable));
            }
            return equations;
        } catch (SQLException e) {
            System.out.println("Problem with query.");
            return null;
        }
    }

    public static List<Equation> getListOfOneVariableValue(String query) {
        List<Equation> equations = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String equation = resultSet.getString("equation");
                equations.add(new Equation(equation));
            }
            return equations;
        } catch (SQLException e) {
            System.out.println("Problem with query.");
            return null;
        }
    }

}
