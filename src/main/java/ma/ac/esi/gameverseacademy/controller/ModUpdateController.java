package ma.ac.esi.gameverseacademy.controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.repository.ModRepository;
import java.io.IOException;
public class ModUpdateController extends HttpServlet {
    private ModRepository repo = new ModRepository();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Mod mod = repo.getModById(id);
        request.setAttribute("mod", mod);
        request.getRequestDispatcher("/update.jsp").forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Mod mod = new Mod();
        mod.setId(Integer.parseInt(request.getParameter("id")));
        mod.setTitle(request.getParameter("title"));
        mod.setCategory(request.getParameter("category"));
        mod.setDescription(request.getParameter("description"));
        mod.setPublisher(request.getParameter("publisher"));
        mod.setPlatform(request.getParameter("platform"));
        mod.setReleaseDate(request.getParameter("releaseDate"));
        String dlStr = request.getParameter("downloads");
        mod.setDownloads(dlStr != null && !dlStr.isEmpty() ? Integer.parseInt(dlStr) : 0);
        String priceStr = request.getParameter("price");
        mod.setPrice(priceStr != null && !priceStr.isEmpty() ? Double.parseDouble(priceStr) : 0.0);
        String metaStr = request.getParameter("metacritic");
        mod.setMetacritic(metaStr != null && !metaStr.isEmpty() ? Integer.parseInt(metaStr) : 0);
        repo.updateMod(mod);
        response.sendRedirect("mods");
    }
}
