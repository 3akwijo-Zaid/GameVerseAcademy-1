package ma.ac.esi.gameverseacademy.repository;
import ma.ac.esi.gameverseacademy.model.Payment;
import ma.ac.esi.gameverseacademy.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class PaymentRepository {
    public int addPayment(Payment p) {
        String sql = "INSERT INTO payments (game_card_id, amount, status, transaction_ref) VALUES (?,?,?,?) RETURNING id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getGameCardId());
            ps.setDouble(2, p.getAmount());
            ps.setString(3, p.getStatus());
            ps.setString(4, p.getTransactionRef());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, m.title as mod_title, gc.user_login FROM payments p " +
                     "JOIN game_cards gc ON p.game_card_id = gc.id " +
                     "JOIN mods m ON gc.mod_id = m.id ORDER BY p.payment_date DESC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Payment p = new Payment();
                p.setId(rs.getInt("id"));
                p.setGameCardId(rs.getInt("game_card_id"));
                p.setAmount(rs.getDouble("amount"));
                p.setStatus(rs.getString("status"));
                p.setPaymentDate(rs.getTimestamp("payment_date"));
                p.setTransactionRef(rs.getString("transaction_ref"));
                p.setModTitle(rs.getString("mod_title"));
                p.setUserLogin(rs.getString("user_login"));
                list.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    public Payment getPaymentById(int id) {
        String sql = "SELECT p.*, m.title as mod_title, gc.user_login FROM payments p " +
                     "JOIN game_cards gc ON p.game_card_id = gc.id " +
                     "JOIN mods m ON gc.mod_id = m.id WHERE p.id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Payment p = new Payment();
                p.setId(rs.getInt("id"));
                p.setGameCardId(rs.getInt("game_card_id"));
                p.setAmount(rs.getDouble("amount"));
                p.setStatus(rs.getString("status"));
                p.setPaymentDate(rs.getTimestamp("payment_date"));
                p.setTransactionRef(rs.getString("transaction_ref"));
                p.setModTitle(rs.getString("mod_title"));
                p.setUserLogin(rs.getString("user_login"));
                return p;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
