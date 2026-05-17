package ma.ac.esi.gameverseacademy.controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.repository.ModRepository;
import ma.ac.esi.gameverseacademy.util.RawgApiUtil;
import ma.ac.esi.gameverseacademy.util.SecurityUtils;
import java.io.IOException;
import java.util.List;
public class RawgEnrichController extends HttpServlet {
    private ModRepository modRepo = new ModRepository();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SecurityUtils.checkAccess(request, response, "ADMIN")) return;
        List<Mod> mods = modRepo.getAllMods();
        int updated = 0;
        for (Mod mod : mods) {
            if (mod.getRawgImage() == null || mod.getRawgImage().isEmpty()) {
                String img = RawgApiUtil.fetchCoverImage(mod.getTitle());
                if (img != null) {
                    modRepo.updateRawgImage(mod.getId(), img);
                    updated++;
                }
            }
        }
        request.setAttribute("message", "Images RAWG mises a jour : " + updated + " / " + mods.size());
        response.sendRedirect(request.getContextPath() + "/mods");
    }
}
