package ma.ac.esi.gameverseacademy.repository;
import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.util.DBUtil;
import java.sql.*;
public class UserRepository {
    public User getUserByCredentials(String login, String password) {
        String sql = "SELECT id, login, is_admin FROM users WHERE login = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setLogin(rs.getString("login"));
                user.setRole(rs.getBoolean("is_admin") ? "ADMIN" : "USER");
                return user;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    public void updatePassword(String login, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE login = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, login);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
