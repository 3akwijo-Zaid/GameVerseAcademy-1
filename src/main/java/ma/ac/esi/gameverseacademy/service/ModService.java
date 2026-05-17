package ma.ac.esi.gameverseacademy.service;

import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.repository.ModRepository;
import java.util.List;

public class ModService {
    private ModRepository modRepository = new ModRepository();

    public List<Mod> getAllMods() {
        return modRepository.getAllMods();
    }

    public boolean submitMod(Mod mod) {
        if (mod.getTitle() == null || mod.getTitle().trim().isEmpty()) {
            System.out.println(">>> ERREUR: titre null ou vide");
            return false;
        }
        try {
            modRepository.addMod(mod);
            return true;
        } catch (Exception e) {
            System.out.println(">>> ERREUR addMod: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}