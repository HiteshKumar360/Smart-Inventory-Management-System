import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/inventory_db",
                    System.getenv("DB_USER"),
                    System.getenv("DB_PASS")
            );

        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }
}
