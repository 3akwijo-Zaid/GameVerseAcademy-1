package ma.ac.esi.gameverseacademy.controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import ma.ac.esi.gameverseacademy.model.*;
import ma.ac.esi.gameverseacademy.repository.*;
import ma.ac.esi.gameverseacademy.service.ModService;
import ma.ac.esi.gameverseacademy.util.PdfGenerator;
import ma.ac.esi.gameverseacademy.util.SecurityUtils;
import java.io.IOException;
import java.util.List;
public class PdfController extends HttpServlet {
    private ModService modService = new ModService();
    private ModRepository modRepo = new ModRepository();
    private RatingRepository ratingRepo = new RatingRepository();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SecurityUtils.checkAccess(request, response, null)) return;
        String type = request.getParameter("type");
        if ("report".equals(type)) {
            List<Mod> mods = modService.getAllMods();
            byte[] pdf = PdfGenerator.generateDownloadReport(mods);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=rapport_downloads.pdf");
            response.getOutputStream().write(pdf);
        } else if ("fiche".equals(type)) {
            int modId = Integer.parseInt(request.getParameter("modId"));
            Mod mod = modRepo.getModById(modId);
            List<Rating> ratings = ratingRepo.getRatingsByMod(modId);
            double avg = ratings.stream().mapToInt(Rating::getStars).average().orElse(0);
            byte[] pdf = PdfGenerator.generateModFiche(mod, avg, ratings.size());
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=fiche_" + modId + ".pdf");
            response.getOutputStream().write(pdf);
        }
    }
}
