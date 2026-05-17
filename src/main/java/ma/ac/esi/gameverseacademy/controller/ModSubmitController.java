package ma.ac.esi.gameverseacademy.controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.service.ModService;
import ma.ac.esi.gameverseacademy.util.RawgApiUtil;
import ma.ac.esi.gameverseacademy.util.SecurityUtils;
import java.io.*;
import java.nio.file.*;
@WebServlet("/ModSubmitController")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
public class ModSubmitController extends HttpServlet {
    private ModService modService = new ModService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SecurityUtils.checkAccess(request, response, null)) return;
        request.getRequestDispatcher("/WEB-INF/views/submitMod.jsp").forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SecurityUtils.checkAccess(request, response, null)) return;
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        
        request.setCharacterEncoding("UTF-8");
        
       
       
        System.out.println(">>> DEBUG content-type: " + request.getContentType());
        
        Mod mod = new Mod();
        mod.setTitle(request.getParameter("title"));
        mod.setCategory(request.getParameter("category"));
        mod.setAuthor(user.getLogin());
        mod.setDescription(request.getParameter("description"));
        mod.setPublisher(request.getParameter("publisher"));
        mod.setPlatform(request.getParameter("platform"));
        mod.setReleaseDate(request.getParameter("releaseDate"));
        String dlStr = request.getParameter("downloads");
        mod.setDownloads((dlStr != null && !dlStr.isEmpty()) ? Integer.parseInt(dlStr) : 0);
        String priceStr = request.getParameter("price");
        mod.setPrice((priceStr != null && !priceStr.isEmpty()) ? Double.parseDouble(priceStr) : 0.0);
        mod.setMetacritic(Integer.parseInt(request.getParameter("metacritic")));
        Part imagePart = request.getPart("image");
        if (imagePart != null && imagePart.getSize() > 0) {
            String uploadDir = getServletContext().getRealPath("/") + "uploads";
            Files.createDirectories(Paths.get(uploadDir));
            String originalName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
            String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
            String fileName = "mod_" + System.currentTimeMillis() + ext;
            try (InputStream in = imagePart.getInputStream()) {
                Files.copy(in, Paths.get(uploadDir, fileName), StandardCopyOption.REPLACE_EXISTING);
            }
            mod.setImagePath("uploads/" + fileName);
        }
        String rawgImg = RawgApiUtil.fetchCoverImage(mod.getTitle());
        mod.setRawgImage(rawgImg);
        if (modService.submitMod(mod)) {
            response.sendRedirect(request.getContextPath() + "/mods");
        } else {
            request.setAttribute("error", "Erreur lors de l'ajout.");
            request.getRequestDispatcher("/WEB-INF/views/submitMod.jsp").forward(request, response);
        }
    }
}
