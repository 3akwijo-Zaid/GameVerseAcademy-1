package ma.ac.esi.gameverseacademy;

import ma.ac.esi.gameverseacademy.model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    // ══════════════════════════════════════════════
    // TESTS UNITAIRES [U]
    // ══════════════════════════════════════════════

    @Test
    @DisplayName("[U01] Constructeur vide → tous les champs sont null")
    void u01_constructeurVide() {
        User u = new User();
        assertNull(u.getEmail());
        assertNull(u.getLogin());
        assertNull(u.getPassword());
        assertNull(u.getRole());
    }

    @Test
    @DisplayName("[U02] Constructeur complet → tous les champs initialisés")
    void u02_constructeurComplet() {
        User u = new User("test@esi.ma", "secret123", "ayoub", "ADMIN");
        assertEquals("test@esi.ma", u.getEmail());
        assertEquals("ayoub", u.getLogin());
        assertEquals("secret123", u.getPassword());
        assertEquals("ADMIN", u.getRole());
    }

    @Test
    @DisplayName("[U03] setEmail / getEmail")
    void u03_email() {
        user.setEmail("user@esi.ma");
        assertEquals("user@esi.ma", user.getEmail());
    }

    @Test
    @DisplayName("[U04] setLogin / getLogin")
    void u04_login() {
        user.setLogin("johndoe");
        assertEquals("johndoe", user.getLogin());
    }

    @Test
    @DisplayName("[U05] setPassword / getPassword")
    void u05_password() {
        user.setPassword("p@ssw0rd");
        assertEquals("p@ssw0rd", user.getPassword());
    }

    @Test
    @DisplayName("[U06] setRole / getRole")
    void u06_role() {
        user.setRole("USER");
        assertEquals("USER", user.getRole());
    }

    @Test
    @DisplayName("[U07] toString contient login et role mais PAS le password")
    void u07_toString() {
        User u = new User("a@b.com", "secret", "alice", "ADMIN");
        String s = u.toString();
        assertTrue(s.contains("alice"),  "toString doit contenir le login");
        assertTrue(s.contains("ADMIN"),  "toString doit contenir le role");
        assertFalse(s.contains("secret"), "toString NE doit PAS contenir le password");
    }

    // ══════════════════════════════════════════════
    // TESTS FONCTIONNELS [F]
    // ══════════════════════════════════════════════

    @ParameterizedTest
    @DisplayName("[F01] Rôles valides acceptés")
    @ValueSource(strings = {"USER", "ADMIN", "MOD", "GUEST"})
    void f01_rolesValides(String role) {
        user.setRole(role);
        assertEquals(role, user.getRole());
    }

    @ParameterizedTest
    @DisplayName("[F02] Emails variés stockés correctement")
    @ValueSource(strings = {"alice@esi.ma", "bob.martin@gmail.com", "user+tag@domain.org"})
    void f02_emailsVaries(String email) {
        user.setEmail(email);
        assertEquals(email, user.getEmail());
    }

    @ParameterizedTest
    @DisplayName("[F03] Constructeur complet via CsvSource")
    @CsvSource({
        "alice@esi.ma, pass1, alice, USER",
        "bob@esi.ma,   pass2, bob,   ADMIN",
        "carol@esi.ma, pass3, carol, MOD"
    })
    void f03_constructeurVariants(String email, String password, String login, String role) {
        User u = new User(email, password, login, role);
        assertEquals(email, u.getEmail());
        assertEquals(login, u.getLogin());
        assertEquals(role,  u.getRole());
    }

    @Test
    @DisplayName("[F04] setLogin écrase la valeur précédente")
    void f04_loginOverwrite() {
        user.setLogin("premier");
        user.setLogin("deuxieme");
        assertEquals("deuxieme", user.getLogin());
    }

    // ══════════════════════════════════════════════
    // TESTS DE PERFORMANCE [P]
    // ══════════════════════════════════════════════

    @Test
    @DisplayName("[P01] 100 000 créations de User < 500 ms")
    @Timeout(2)
    void p01_creation100k() {
        long debut = System.currentTimeMillis();
        for (int i = 0; i < 100_000; i++) {
            User u = new User("e" + i + "@esi.ma", "pwd" + i, "login" + i, "USER");
            assertNotNull(u);
        }
        long duree = System.currentTimeMillis() - debut;
        assertTrue(duree < 500, "100 000 créations doivent durer < 500 ms — réel : " + duree + " ms");
    }

    @Test
    @DisplayName("[P02] 200 000 appels get/set < 300 ms")
    @Timeout(2)
    void p02_getSetPerf() {
        long debut = System.currentTimeMillis();
        for (int i = 0; i < 200_000; i++) {
            user.setLogin("login" + i);
            assertNotNull(user.getLogin());
        }
        long duree = System.currentTimeMillis() - debut;
        assertTrue(duree < 300, "200 000 get/set doivent durer < 300 ms — réel : " + duree + " ms");
    }
}
