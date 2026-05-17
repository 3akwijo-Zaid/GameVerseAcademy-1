package ma.ac.esi.gameverseacademy.repository;
import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ModRepository {
    private Mod mapMod(ResultSet rs) throws SQLException {
        Mod m = new Mod();
        m.setId(rs.getInt("id"));
        m.setTitle(rs.getString("title"));
        m.setCategory(rs.getString("category"));
        m.setAuthor(rs.getString("author"));
        m.setDescription(rs.getString("description"));
        m.setDownloads(rs.getInt("downloads"));
        m.setCreatedAt(rs.getTimestamp("created_at"));
        m.setDeveloper(rs.getString("developer"));
        m.setPublisher(rs.getString("publisher"));
        m.setPlatform(rs.getString("platform"));
        m.setReleaseDate(rs.getString("release_date"));
        m.setMetacritic(rs.getInt("metacritic"));
        m.setImagePath(rs.getString("image_path"));
        m.setPrice(rs.getDouble("price"));
        m.setRawgImage(rs.getString("rawg_image"));
        return m;
    }
    public List<Mod> getAllMods() {
        List<Mod> mods = new ArrayList<>();
        String sql = "SELECT m.*, COALESCE(AVG(r.stars),0) as avg_rating FROM mods m " +
                     "LEFT JOIN ratings r ON m.id = r.mod_id GROUP BY m.id ORDER BY m.id ASC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Mod mod = mapMod(rs);
                mod.setAverageRating(rs.getDouble("avg_rating"));
                mods.add(mod);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return mods;
    }
    public Mod getModById(int id) {
        String sql = "SELECT m.*, COALESCE(AVG(r.stars),0) as avg_rating FROM mods m " +
                     "LEFT JOIN ratings r ON m.id = r.mod_id WHERE m.id = ? GROUP BY m.id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Mod mod = mapMod(rs);
                mod.setAverageRating(rs.getDouble("avg_rating"));
                return mod;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    public void addMod(Mod mod) throws SQLException {
        String sql = "INSERT INTO mods (title, category, author, description, publisher, downloads, price, image_path, rawg_image, metacritic, platform, release_date) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mod.getTitle());
            ps.setString(2, mod.getCategory());
            ps.setString(3, mod.getAuthor());
            ps.setString(4, mod.getDescription());
            ps.setString(5, mod.getPublisher());
            ps.setInt(6, mod.getDownloads());
            ps.setDouble(7, mod.getPrice());
            ps.setString(8, mod.getImagePath());
            ps.setString(9, mod.getRawgImage());
            ps.setInt(10, mod.getMetacritic());
            ps.setString(11, mod.getPlatform());
            ps.setString(12, mod.getReleaseDate());
            ps.executeUpdate();
        }
    }
    public void updateMod(Mod mod) {
        String sql = "UPDATE mods SET title=?, category=?, description=?, downloads=?, price=?, publisher=?, platform=?, release_date=?, metacritic=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mod.getTitle());
            ps.setString(2, mod.getCategory());
            ps.setString(3, mod.getDescription());
            ps.setInt(4, mod.getDownloads());
            ps.setDouble(5, mod.getPrice());
            ps.setString(6, mod.getPublisher());
            ps.setString(7, mod.getPlatform());
            ps.setString(8, mod.getReleaseDate());
            ps.setInt(9, mod.getMetacritic());
            ps.setInt(10, mod.getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public void incrementDownloads(int modId) {
        String sql = "UPDATE mods SET downloads = downloads + 1 WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public void deleteMod(int id) {
        String sql = "DELETE FROM mods WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public void updateRawgImage(int modId, String imageUrl) {
        String sql = "UPDATE mods SET rawg_image=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, imageUrl);
            ps.setInt(2, modId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
