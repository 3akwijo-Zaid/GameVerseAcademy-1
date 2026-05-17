package ma.ac.esi.gameverseacademy.model;
import java.sql.Timestamp;
public class GameCard {
    private int id;
    private int modId;
    private String userLogin;
    private String cardHolder;
    private String cardNumberLast4;
    private double price;
    private Timestamp purchasedAt;
    private String modTitle;

    public GameCard() {}
    public int getId() { return id; }
    public int getModId() { return modId; }
    public String getUserLogin() { return userLogin; }
    public String getCardHolder() { return cardHolder; }
    public String getCardNumberLast4() { return cardNumberLast4; }
    public double getPrice() { return price; }
    public Timestamp getPurchasedAt() { return purchasedAt; }
    public String getModTitle() { return modTitle; }
    public void setId(int id) { this.id = id; }
    public void setModId(int modId) { this.modId = modId; }
    public void setUserLogin(String userLogin) { this.userLogin = userLogin; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }
    public void setCardNumberLast4(String cardNumberLast4) { this.cardNumberLast4 = cardNumberLast4; }
    public void setPrice(double price) { this.price = price; }
    public void setPurchasedAt(Timestamp purchasedAt) { this.purchasedAt = purchasedAt; }
    public void setModTitle(String modTitle) { this.modTitle = modTitle; }
}
