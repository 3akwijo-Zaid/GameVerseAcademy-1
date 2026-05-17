package ma.ac.esi.gameverseacademy;

import ma.ac.esi.gameverseacademy.model.GameCard;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class GameCardTest {

    private GameCard card;

    @BeforeEach
    void setUp() {
        card = new GameCard();
    }

    // ══════════════════════════════════════════════
    // TESTS UNITAIRES [U]
    // ══════════════════════════════════════════════

    @Test
    @DisplayName("[U01] Constructeur vide → valeurs par défaut")
    void u01_constructeurVide() {
        GameCard gc = new GameCard();
        assertEquals(0,   gc.getId());
        assertEquals(0,   gc.getModId());
        assertNull(gc.getUserLogin());
        assertNull(gc.getCardHolder());
        assertNull(gc.getCardNumberLast4());
        assertEquals(0.0, gc.getPrice(), 0.001);
        assertNull(gc.getPurchasedAt());
        assertNull(gc.getModTitle());
    }

    @Test
    @DisplayName("[U02] setId / getId")
    void u02_id() {
        card.setId(99);
        assertEquals(99, card.getId());
    }

    @Test
    @DisplayName("[U03] setModId / getModId")
    void u03_modId() {
        card.setModId(3);
        assertEquals(3, card.getModId());
    }

    @Test
    @DisplayName("[U04] setUserLogin / getUserLogin")
    void u04_userLogin() {
        card.setUserLogin("bob");
        assertEquals("bob", card.getUserLogin());
    }

    @Test
    @DisplayName("[U05] setCardHolder / getCardHolder")
    void u05_cardHolder() {
        card.setCardHolder("Ayoub Bouhaddou");
        assertEquals("Ayoub Bouhaddou", card.getCardHolder());
    }

    @Test
    @DisplayName("[U06] setCardNumberLast4 / getCardNumberLast4")
    void u06_cardNumberLast4() {
        card.setCardNumberLast4("1234");
        assertEquals("1234", card.getCardNumberLast4());
    }

    @Test
    @DisplayName("[U07] setPrice / getPrice")
    void u07_price() {
        card.setPrice(19.99);
        assertEquals(19.99, card.getPrice(), 0.001);
    }

    @Test
    @DisplayName("[U08] setPurchasedAt / getPurchasedAt")
    void u08_purchasedAt() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        card.setPurchasedAt(ts);
        assertEquals(ts, card.getPurchasedAt());
    }

    @Test
    @DisplayName("[U09] setModTitle / getModTitle")
    void u09_modTitle() {
        card.setModTitle("Witcher HD");
        assertEquals("Witcher HD", card.getModTitle());
    }

    // ══════════════════════════════════════════════
    // TESTS FONCTIONNELS [F]
    // ══════════════════════════════════════════════

    @ParameterizedTest
    @DisplayName("[F01] Derniers 4 chiffres de carte valides")
    @ValueSource(strings = {"0000", "1234", "9999", "4242"})
    void f01_cardNumberLast4(String last4) {
        card.setCardNumberLast4(last4);
        assertEquals(last4, card.getCardNumberLast4());
        assertEquals(4, card.getCardNumberLast4().length());
    }

    @ParameterizedTest
    @DisplayName("[F02] GameCard complète via CsvSource")
    @CsvSource({
        "alice, Alice Martin, 1234, Mod Alpha, 9.99",
        "bob,   Bob Dupont,   5678, Mod Beta,  14.99",
        "carol, Carol Lee,    4242, Mod Gamma, 4.99"
    })
    void f02_gameCardComplete(String login, String holder, String last4, String title, double price) {
        card.setUserLogin(login);
        card.setCardHolder(holder);
        card.setCardNumberLast4(last4);
        card.setModTitle(title);
        card.setPrice(price);
        assertEquals(login,  card.getUserLogin());
        assertEquals(holder, card.getCardHolder());
        assertEquals(last4,  card.getCardNumberLast4());
        assertEquals(title,  card.getModTitle());
        assertEquals(price,  card.getPrice(), 0.001);
    }

    @Test
    @DisplayName("[F03] Deux GameCard ont des IDs indépendants")
    void f03_idsIndependants() {
        GameCard gc1 = new GameCard();
        GameCard gc2 = new GameCard();
        gc1.setId(1);
        gc2.setId(2);
        assertNotEquals(gc1.getId(), gc2.getId());
    }

    @Test
    @DisplayName("[F04] Prix modifiable après création")
    void f04_prixModifiable() {
        card.setPrice(9.99);
        assertEquals(9.99, card.getPrice(), 0.001);
        card.setPrice(19.99);
        assertEquals(19.99, card.getPrice(), 0.001);
    }

    @Test
    @DisplayName("[F05] modId et userLogin liés correctement")
    void f05_modIdEtLogin() {
        card.setModId(42);
        card.setUserLogin("testuser");
        assertEquals(42,         card.getModId());
        assertEquals("testuser", card.getUserLogin());
    }

    // ══════════════════════════════════════════════
    // TESTS DE PERFORMANCE [P]
    // ══════════════════════════════════════════════

    @Test
    @DisplayName("[P01] 100 000 créations de GameCard < 500 ms")
    @Timeout(2)
    void p01_creation100k() {
        long debut = System.currentTimeMillis();
        for (int i = 0; i < 100_000; i++) {
            GameCard gc = new GameCard();
            gc.setId(i);
            gc.setPrice(i * 0.5);
            gc.setCardNumberLast4(String.format("%04d", i % 10000));
            assertNotNull(gc);
        }
        long duree = System.currentTimeMillis() - debut;
        assertTrue(duree < 500, "100 000 créations doivent durer < 500 ms — réel : " + duree + " ms");
    }

    @Test
    @DisplayName("[P02] 200 000 appels getPrice / setPrice < 300 ms")
    @Timeout(2)
    void p02_getSetPricePerf() {
        long debut = System.currentTimeMillis();
        for (int i = 0; i < 200_000; i++) {
            card.setPrice(i * 0.01);
            assertTrue(card.getPrice() >= 0);
        }
        long duree = System.currentTimeMillis() - debut;
        assertTrue(duree < 300, "200 000 get/set price doivent durer < 300 ms — réel : " + duree + " ms");
    }
}
