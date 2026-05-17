package ma.ac.esi.gameverseacademy;

import ma.ac.esi.gameverseacademy.model.Payment;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
    }

    // ══════════════════════════════════════════════
    // TESTS UNITAIRES [U]
    // ══════════════════════════════════════════════

    @Test
    @DisplayName("[U01] Constructeur vide → valeurs par défaut")
    void u01_constructeurVide() {
        Payment p = new Payment();
        assertEquals(0,   p.getId());
        assertEquals(0,   p.getGameCardId());
        assertEquals(0.0, p.getAmount(), 0.001);
        assertNull(p.getStatus());
        assertNull(p.getPaymentDate());
        assertNull(p.getTransactionRef());
        assertNull(p.getModTitle());
        assertNull(p.getUserLogin());
    }

    @Test
    @DisplayName("[U02] setId / getId")
    void u02_id() {
        payment.setId(42);
        assertEquals(42, payment.getId());
    }

    @Test
    @DisplayName("[U03] setGameCardId / getGameCardId")
    void u03_gameCardId() {
        payment.setGameCardId(7);
        assertEquals(7, payment.getGameCardId());
    }

    @Test
    @DisplayName("[U04] setAmount / getAmount")
    void u04_amount() {
        payment.setAmount(29.99);
        assertEquals(29.99, payment.getAmount(), 0.001);
    }

    @Test
    @DisplayName("[U05] setStatus / getStatus")
    void u05_status() {
        payment.setStatus("SUCCESS");
        assertEquals("SUCCESS", payment.getStatus());
    }

    @Test
    @DisplayName("[U06] setPaymentDate / getPaymentDate")
    void u06_paymentDate() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        payment.setPaymentDate(ts);
        assertEquals(ts, payment.getPaymentDate());
    }

    @Test
    @DisplayName("[U07] setTransactionRef / getTransactionRef")
    void u07_transactionRef() {
        payment.setTransactionRef("TXN-2024-001");
        assertEquals("TXN-2024-001", payment.getTransactionRef());
    }

    @Test
    @DisplayName("[U08] setModTitle / getModTitle")
    void u08_modTitle() {
        payment.setModTitle("Skyrim Ultra HD");
        assertEquals("Skyrim Ultra HD", payment.getModTitle());
    }

    @Test
    @DisplayName("[U09] setUserLogin / getUserLogin")
    void u09_userLogin() {
        payment.setUserLogin("ayoub");
        assertEquals("ayoub", payment.getUserLogin());
    }

    // ══════════════════════════════════════════════
    // TESTS FONCTIONNELS [F]
    // ══════════════════════════════════════════════

    @ParameterizedTest
    @DisplayName("[F01] Statuts valides de paiement")
    @ValueSource(strings = {"SUCCESS", "FAILED", "PENDING", "REFUNDED"})
    void f01_statutsValides(String status) {
        payment.setStatus(status);
        assertEquals(status, payment.getStatus());
    }

    @ParameterizedTest
    @DisplayName("[F02] Montants variés stockés correctement")
    @CsvSource({"0.0", "9.99", "99.99", "999.99"})
    void f02_montantsVaries(double amount) {
        payment.setAmount(amount);
        assertEquals(amount, payment.getAmount(), 0.001);
    }

    @ParameterizedTest
    @DisplayName("[F03] Références de transaction variées")
    @ValueSource(strings = {"TXN-001", "TXN-ABC-2024", "REF-999999"})
    void f03_transactionRefs(String ref) {
        payment.setTransactionRef(ref);
        assertEquals(ref, payment.getTransactionRef());
    }

    @Test
    @DisplayName("[F04] Statut peut changer de PENDING à SUCCESS")
    void f04_changementStatut() {
        payment.setStatus("PENDING");
        assertEquals("PENDING", payment.getStatus());
        payment.setStatus("SUCCESS");
        assertEquals("SUCCESS", payment.getStatus());
    }

    @Test
    @DisplayName("[F05] Deux Payment ont des IDs indépendants")
    void f05_idsIndependants() {
        Payment p1 = new Payment();
        Payment p2 = new Payment();
        p1.setId(1);
        p2.setId(2);
        assertNotEquals(p1.getId(), p2.getId());
    }

    // ══════════════════════════════════════════════
    // TESTS DE PERFORMANCE [P]
    // ══════════════════════════════════════════════

    @Test
    @DisplayName("[P01] 100 000 créations de Payment < 500 ms")
    @Timeout(2)
    void p01_creation100k() {
        long debut = System.currentTimeMillis();
        for (int i = 0; i < 100_000; i++) {
            Payment p = new Payment();
            p.setId(i);
            p.setAmount(i * 1.5);
            p.setStatus("SUCCESS");
            assertNotNull(p);
        }
        long duree = System.currentTimeMillis() - debut;
        assertTrue(duree < 500, "100 000 créations doivent durer < 500 ms — réel : " + duree + " ms");
    }

    @Test
    @DisplayName("[P02] 200 000 appels getAmount / setAmount < 300 ms")
    @Timeout(2)
    void p02_getSetAmountPerf() {
        long debut = System.currentTimeMillis();
        for (int i = 0; i < 200_000; i++) {
            payment.setAmount(i * 0.99);
            assertTrue(payment.getAmount() >= 0);
        }
        long duree = System.currentTimeMillis() - debut;
        assertTrue(duree < 300, "200 000 get/set amount doivent durer < 300 ms — réel : " + duree + " ms");
    }
}
