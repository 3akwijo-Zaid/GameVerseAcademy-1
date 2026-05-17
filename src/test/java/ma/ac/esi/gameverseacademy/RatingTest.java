package ma.ac.esi.gameverseacademy;

import ma.ac.esi.gameverseacademy.model.Rating;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class RatingTest {

    private Rating rating;

    @BeforeEach
    void setUp() {
        rating = new Rating();
    }

    // ══════════════════════════════════════════════
    // TESTS UNITAIRES [U]
    // ══════════════════════════════════════════════

    @Test
    @DisplayName("[U01] Constructeur vide → valeurs par défaut")
    void u01_constructeurVide() {
        Rating r = new Rating();
        assertEquals(0, r.getId());
        assertEquals(0, r.getModId());
        assertNull(r.getUserLogin());
        assertEquals(0, r.getStars());
        assertNull(r.getComment());
        assertNull(r.getRatedAt());
    }

    @Test
    @DisplayName("[U02] setId / getId")
    void u02_id() {
        rating.setId(10);
        assertEquals(10, rating.getId());
    }

    @Test
    @DisplayName("[U03] setModId / getModId")
    void u03_modId() {
        rating.setModId(5);
        assertEquals(5, rating.getModId());
    }

    @Test
    @DisplayName("[U04] setUserLogin / getUserLogin")
    void u04_userLogin() {
        rating.setUserLogin("alice");
        assertEquals("alice", rating.getUserLogin());
    }

    @Test
    @DisplayName("[U05] setStars / getStars")
    void u05_stars() {
        rating.setStars(4);
        assertEquals(4, rating.getStars());
    }

    @Test
    @DisplayName("[U06] setComment / getComment")
    void u06_comment() {
        rating.setComment("Excellent mod !");
        assertEquals("Excellent mod !", rating.getComment());
    }

    @Test
    @DisplayName("[U07] setRatedAt / getRatedAt")
    void u07_ratedAt() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        rating.setRatedAt(ts);
        assertEquals(ts, rating.getRatedAt());
    }

    // ══════════════════════════════════════════════
    // TESTS FONCTIONNELS [F]
    // ══════════════════════════════════════════════

    @ParameterizedTest
    @DisplayName("[F01] Notes de 1 à 5 étoiles acceptées")
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void f01_etoilesValides(int stars) {
        rating.setStars(stars);
        assertEquals(stars, rating.getStars());
        assertTrue(rating.getStars() >= 1 && rating.getStars() <= 5);
    }

    @ParameterizedTest
    @DisplayName("[F02] Rating complet via CsvSource (login, stars, commentaire)")
    @CsvSource({
        "alice, 5, Parfait !",
        "bob,   3, Moyen",
        "carol, 1, Décevant"
    })
    void f02_ratingsComplets(String login, int stars, String comment) {
        rating.setUserLogin(login);
        rating.setStars(stars);
        rating.setComment(comment);
        assertEquals(login,   rating.getUserLogin());
        assertEquals(stars,   rating.getStars());
        assertEquals(comment, rating.getComment());
    }

    @Test
    @DisplayName("[F03] Rating modifiable après création")
    void f03_modificationRating() {
        rating.setStars(2);
        rating.setComment("Pas terrible");
        rating.setStars(5);
        rating.setComment("Excellent après mise à jour !");
        assertEquals(5, rating.getStars());
        assertEquals("Excellent après mise à jour !", rating.getComment());
    }

    @Test
    @DisplayName("[F04] Deux Rating ont des timestamps distincts")
    void f04_timestampDistinct() {
        Rating r1 = new Rating();
        Rating r2 = new Rating();
        r1.setRatedAt(new Timestamp(1000L));
        r2.setRatedAt(new Timestamp(2000L));
        assertNotEquals(r1.getRatedAt(), r2.getRatedAt());
    }

    // ══════════════════════════════════════════════
    // TESTS DE PERFORMANCE [P]
    // ══════════════════════════════════════════════

    @Test
    @DisplayName("[P01] 100 000 créations de Rating < 500 ms")
    @Timeout(2)
    void p01_creation100k() {
        long debut = System.currentTimeMillis();
        for (int i = 0; i < 100_000; i++) {
            Rating r = new Rating();
            r.setStars((i % 5) + 1);
            r.setUserLogin("user" + i);
            assertNotNull(r);
        }
        long duree = System.currentTimeMillis() - debut;
        assertTrue(duree < 500, "100 000 créations doivent durer < 500 ms — réel : " + duree + " ms");
    }

    @Test
    @DisplayName("[P02] 150 000 appels getStars / setStars < 200 ms")
    @Timeout(2)
    void p02_getSetStarsPerf() {
        long debut = System.currentTimeMillis();
        for (int i = 0; i < 150_000; i++) {
            rating.setStars((i % 5) + 1);
            int v = rating.getStars();
            assertTrue(v >= 1 && v <= 5);
        }
        long duree = System.currentTimeMillis() - debut;
        assertTrue(duree < 200, "150 000 get/set stars doivent durer < 200 ms — réel : " + duree + " ms");
    }
}
