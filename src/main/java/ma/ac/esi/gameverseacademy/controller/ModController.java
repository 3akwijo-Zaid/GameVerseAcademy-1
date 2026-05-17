package ma.ac.esi.gameverseacademy.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ac.esi.gameverseacademy.model.Mod;
import ma.ac.esi.gameverseacademy.service.ModService;
import ma.ac.esi.gameverseacademy.util.SecurityUtils;
import java.io.IOException;
import java.util.List;

@WebServlet("/mods")
public class ModController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        if (!SecurityUtils.isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }

        ModService modService = new ModService();
        List<Mod> mods = modService.getAllMods();
        
        request.setAttribute("mods", mods);
        request.getRequestDispatcher("/WEB-INF/views/mods.jsp").forward(request, response);
    }
}