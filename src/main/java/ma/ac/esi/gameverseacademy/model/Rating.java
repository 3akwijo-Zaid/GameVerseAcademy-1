package ma.ac.esi.gameverseacademy.model;
import java.sql.Timestamp;
public class Rating {
    private int id;
    private int modId;
    private String userLogin;
    private int stars;
    private String comment;
    private Timestamp ratedAt;

    public Rating() {}
    public int getId() { return id; }
    public int getModId() { return modId; }
    public String getUserLogin() { return userLogin; }
    public int getStars() { return stars; }
    public String getComment() { return comment; }
    public Timestamp getRatedAt() { return ratedAt; }
    public void setId(int id) { this.id = id; }
    public void setModId(int modId) { this.modId = modId; }
    public void setUserLogin(String userLogin) { this.userLogin = userLogin; }
    public void setStars(int stars) { this.stars = stars; }
    public void setComment(String comment) { this.comment = comment; }
    public void setRatedAt(Timestamp ratedAt) { this.ratedAt = ratedAt; }
}
