package ma.ac.esi.gameverseacademy.model;
import java.sql.Timestamp;
public class Payment {
    private int id;
    private int gameCardId;
    private double amount;
    private String status;
    private Timestamp paymentDate;
    private String transactionRef;
    private String modTitle;
    private String userLogin;

    public Payment() {}
    public int getId() { return id; }
    public int getGameCardId() { return gameCardId; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public Timestamp getPaymentDate() { return paymentDate; }
    public String getTransactionRef() { return transactionRef; }
    public String getModTitle() { return modTitle; }
    public String getUserLogin() { return userLogin; }
    public void setId(int id) { this.id = id; }
    public void setGameCardId(int gameCardId) { this.gameCardId = gameCardId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentDate(Timestamp paymentDate) { this.paymentDate = paymentDate; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public void setModTitle(String modTitle) { this.modTitle = modTitle; }
    public void setUserLogin(String userLogin) { this.userLogin = userLogin; }
}
