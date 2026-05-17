package ma.ac.esi.gameverseacademy;

import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.service.ModService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ModTest {

    private Mod mod;

    @BeforeEach
    void setUp() {
        mod = new Mod();
    }

    // ══════════════════════════════════════════════
    // TESTS UNITAIRES [U]
    // ══════════════════════════════════════════════

    @Test
    @DisplayName("[U01] Constructeur vide → id=0, champs String=null")
    void u01_constructeurVide() {
        assertEquals(0, mod.getId());
        assertNull(mod.getTitle());
        assertNull(mod.getCategory());
        assertNull(mod.getImagePath());
        assertNull(mod.getRawgImage());
    }

    @Test
    @DisplayName("[U02] Constructeur 15-args → tous les champs initialisés")
    void u02_constructeur15Args() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Mod m = new Mod(1, "Skyrim Ultra", "RPG", "Ayoub", "Super mod",
                500, ts, "BethSoft", "BethPub", "PC",
                "2020-01-01", 95, "/img/sky.png", 9.99, "https://rawg.io/img.jpg");

        assertEquals(1,                      m.getId());
        assertEquals("Skyrim Ultra",         m.getTitle());
        assertEquals("RPG",                  m.getCategory());
        assertEquals("Ayoub",                m.getAuthor());
        assertEquals(500,                    m.getDownloads());
        assertEquals(95,                     m.getMetacritic());
        assertEquals(9.99,                   m.getPrice(), 0.001);
        assertEquals("/img/sky.png",         m.getImagePath());
        assertEquals("https://rawg.io/img.jpg", m.getRawgImage());
    }

    @Test
    @DisplayName("[U03] getDisplayImage → préfère imagePath si non vide")
    void u03_displayImagePrefereImagePath() {
        mod.setImagePath("/local/image.png");
        mod.setRawgImage("https://rawg.io/fallback.jpg");
        assertEquals("/local/image.png", mod.getDisplayImage());
    }

    @Test
    @DisplayName("[U04] getDisplayImage → fallback rawgImage si imagePath null")
    void u04_displayImageFallbackNull() {
        mod.setImagePath(null);
        mod.setRawgImage("https://rawg.io/img.jpg");
        assertEquals("https://rawg.io/img.jpg", mod.getDisplayImage());
    }

    @Test
    @DisplayName("[U05] getDisplayImage → fallback rawgImage si imagePath vide")
    void u05_displayImageFallbackVide() {
        mod.setImagePath("");
        mod.setRawgImage("https://rawg.io/img.jpg");
        assertEquals("https://rawg.io/img.jpg", mod.getDisplayImage());
    }

    @Test
    @DisplayName("[U06] getDisplayImage → null si les deux sont null")
    void u06_displayImageDeuxNull() {
        mod.setImagePath(null);
        mod.setRawgImage(null);
        assertNull(mod.getDisplayImage());
    }

    @Test
    @DisplayName("[U07] getDisplayImage → null si les deux sont vides")
    void u07_displayImageDeuxVides() {
        mod.setImagePath("");
        mod.setRawgImage("");
        assertNull(mod.getDisplayImage());
    }

    @Test
    @DisplayName("[U08] submitMod → false si titre null")
    void u08_submitModTitreNull() {
        ModService service = new ModService();
        mod.setTitle(null);
        assertFalse(service.submitMod(mod));
    }

    @Test
    @DisplayName("[U09] submitMod → false si titre vide")
    void u09_submitModTitreVide() {
        ModService service = new ModService();
        mod.setTitle("");
        assertFalse(service.submitMod(mod));
    }

    @Test
    @DisplayName("[U10] submitMod → false si titre whitespace uniquement")
    void u10_submitModTitreWhitespace() {
        ModService service = new ModService();
        mod.setTitle("   ");
        assertFalse(service.submitMod(mod));
    }

    // ══════════════════════════════════════════════
    // TESTS FONCTIONNELS [F]
    // ══════════════════════════════════════════════

    @ParameterizedTest
    @DisplayName("[F01] submitMod retourne false pour tout titre invalide")
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void f01_submitModTitresInvalides(String titre) {
        ModService service = new ModService();
        mod.setTitle(titre);
        assertFalse(service.submitMod(mod));
    }

    @ParameterizedTest
    @DisplayName("[F02] getDisplayImage avec URLs rawg variées")
    @ValueSource(strings = {
        "https://media.rawg.io/media/games/img1.jpg",
        "https://media.rawg.io/media/games/img2.png",
        "/uploads/local.jpg"
    })
    void f02_getDisplayImageRawgVariants(String rawgUrl) {
        mod.setImagePath(null);
        mod.setRawgImage(rawgUrl);
        assertEquals(rawgUrl, mod.getDisplayImage());
    }

    @ParameterizedTest
    @DisplayName("[F03] Catégories, téléchargements, prix, metacritic via CsvSource")
    @CsvSource({
        "RPG, 100, 9.99,  85",
        "FPS, 200, 14.99, 72",
        "RTS, 50,  4.99,  91"
    })
    void f03_categoriesEtPrix(String cat, int dl, double price, int meta) {
        mod.setCategory(cat);
        mod.setDownloads(dl);
        mod.setPrice(price);
        mod.setMetacritic(meta);
        assertEquals(cat,   mod.getCategory());
        assertEquals(dl,    mod.getDownloads());
        assertEquals(price, mod.getPrice(), 0.001);
        assertEquals(meta,  mod.getMetacritic());
    }

    @Test
    @DisplayName("[F04] submitMod titre valide → pas d'exception non gérée (sans BD → false)")
    void f04_submitModTitreValide() {
        ModService service = new ModService();
        mod.setTitle("Mod valide sans BD");
        // Sans BD, addMod() lève une exception catchée dans le service → retourne false
        // Ce test vérifie seulement qu'aucune exception n'est propagée
        assertDoesNotThrow(() -> service.submitMod(mod));
    }

    // ══════════════════════════════════════════════
    // TESTS DE PERFORMANCE [P]
    // ══════════════════════════════════════════════

    @Test
    @DisplayName("[P01] 50 000 appels getDisplayImage < 200 ms")
    @Timeout(2)
    void p01_getDisplayImagePerf() {
        mod.setImagePath("/img/test.png");
        mod.setRawgImage("https://rawg.io/img.jpg");
        long debut = System.currentTimeMillis();
        for (int i = 0; i < 50_000; i++) {
            assertNotNull(mod.getDisplayImage());
        }
        long duree = System.currentTimeMillis() - debut;
        assertTrue(duree < 200, "50 000 appels getDisplayImage doivent durer < 200 ms — réel : " + duree + " ms");
    }

    @Test
    @DisplayName("[P02] 10 000 submitMod titres invalides < 2000 ms")
    @Timeout(5)
    void p02_submitModPerfInvalides() {
        ModService service = new ModService();
        long debut = System.currentTimeMillis();
        for (int i = 0; i < 10_000; i++) {
            Mod m = new Mod();
            m.setTitle(i % 2 == 0 ? null : "");
            assertFalse(service.submitMod(m));
        }
        long duree = System.currentTimeMillis() - debut;
        assertTrue(duree < 2000, "10 000 submitMod invalides doivent durer < 2000 ms — réel : " + duree + " ms");
    }
}
