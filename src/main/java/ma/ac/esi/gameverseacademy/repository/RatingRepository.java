package ma.ac.esi.gameverseacademy.repository;
import ma.ac.esi.gameverseacademy.model.Rating;
import ma.ac.esi.gameverseacademy.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class RatingRepository {
    public void addOrUpdateRating(Rating r) {
        String sql = "INSERT INTO ratings (mod_id, user_login, stars, comment) VALUES (?,?,?,?) " +
                     "ON CONFLICT (mod_id, user_login) DO UPDATE SET stars=EXCLUDED.stars, comment=EXCLUDED.comment";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getModId());
            ps.setString(2, r.getUserLogin());
            ps.setInt(3, r.getStars());
            ps.setString(4, r.getComment());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public List<Rating> getRatingsByMod(int modId) {
        List<Rating> list = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE mod_id=? ORDER BY rated_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Rating r = new Rating();
                r.setId(rs.getInt("id"));
                r.setModId(rs.getInt("mod_id"));
                r.setUserLogin(rs.getString("user_login"));
                r.setStars(rs.getInt("stars"));
                r.setComment(rs.getString("comment"));
                r.setRatedAt(rs.getTimestamp("rated_at"));
                list.add(r);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
