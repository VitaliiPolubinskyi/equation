package database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    public static Connection getConnection() {
        String URL;
        String USERNAME;
        String PASSWORD;
        Connection connection;
        FileInputStream fis;

        Properties properties = new Properties();

        try {
            fis = new FileInputStream("src/main/resources/config.properties");
            properties.load(fis);
            URL = properties.getProperty("URL");
            USERNAME = properties.getProperty("USERNAME");
            PASSWORD = properties.getProperty("PASSWORD");
        } catch (FileNotFoundException e) {
            System.out.println("A file with parameters is missing.");
            return null;
        } catch (IOException e) {
            System.out.println("Error during parameters loading occurred.");
            return null;
        }

        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            return connection;
        } catch (SQLException e) {
            System.out.println("Can't connect to DataBase.");
            return null;
        }
    }
}
