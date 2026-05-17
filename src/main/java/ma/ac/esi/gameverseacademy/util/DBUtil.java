package ma.ac.esi.gameverseacademy.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBUtil {
    private static final String URL = "jdbc:postgresql://dpg-d7l35vm47okc73b5p6qg-a.frankfurt-postgres.render.com:5432/gameverseacademy_pkye";
    private static final String USER = "gameverseacademy_pkye_user";
    private static final String PASSWORD = "xILxTNTLw0ZzHnSYQ00bkqyeaTl9clui";
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}