package ma.ac.esi.gameverseacademy.repository;
import ma.ac.esi.gameverseacademy.model.GameCard;
import ma.ac.esi.gameverseacademy.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class GameCardRepository {
    public List<GameCard> getAllCards() {
        List<GameCard> cards = new ArrayList<>();
        String sql = "SELECT gc.*, m.title as mod_title FROM game_cards gc JOIN mods m ON gc.mod_id = m.id ORDER BY gc.id DESC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                GameCard gc = new GameCard();
                gc.setId(rs.getInt("id"));
                gc.setModId(rs.getInt("mod_id"));
                gc.setUserLogin(rs.getString("user_login"));
                gc.setCardHolder(rs.getString("card_holder"));
                gc.setCardNumberLast4(rs.getString("card_number_last4"));
                gc.setPrice(rs.getDouble("price"));
                gc.setPurchasedAt(rs.getTimestamp("purchased_at"));
                gc.setModTitle(rs.getString("mod_title"));
                cards.add(gc);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return cards;
    }
    public List<GameCard> getCardsByUser(String userLogin) {
        List<GameCard> cards = new ArrayList<>();
        String sql = "SELECT gc.*, m.title as mod_title FROM game_cards gc JOIN mods m ON gc.mod_id = m.id WHERE gc.user_login=? ORDER BY gc.id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userLogin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                GameCard gc = new GameCard();
                gc.setId(rs.getInt("id"));
                gc.setModId(rs.getInt("mod_id"));
                gc.setUserLogin(rs.getString("user_login"));
                gc.setCardHolder(rs.getString("card_holder"));
                gc.setCardNumberLast4(rs.getString("card_number_last4"));
                gc.setPrice(rs.getDouble("price"));
                gc.setPurchasedAt(rs.getTimestamp("purchased_at"));
                gc.setModTitle(rs.getString("mod_title"));
                cards.add(gc);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return cards;
    }
    public int addCard(GameCard gc) {
        String sql = "INSERT INTO game_cards (mod_id, user_login, card_holder, card_number_last4, price) VALUES (?,?,?,?,?) RETURNING id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gc.getModId());
            ps.setString(2, gc.getUserLogin());
            ps.setString(3, gc.getCardHolder());
            ps.setString(4, gc.getCardNumberLast4());
            ps.setDouble(5, gc.getPrice());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
    public void deleteCard(int id) {
        String sql = "DELETE FROM game_cards WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public GameCard getCardById(int id) {
        String sql = "SELECT gc.*, m.title as mod_title FROM game_cards gc JOIN mods m ON gc.mod_id = m.id WHERE gc.id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                GameCard gc = new GameCard();
                gc.setId(rs.getInt("id"));
                gc.setModId(rs.getInt("mod_id"));
                gc.setUserLogin(rs.getString("user_login"));
                gc.setCardHolder(rs.getString("card_holder"));
                gc.setCardNumberLast4(rs.getString("card_number_last4"));
                gc.setPrice(rs.getDouble("price"));
                gc.setPurchasedAt(rs.getTimestamp("purchased_at"));
                gc.setModTitle(rs.getString("mod_title"));
                return gc;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
