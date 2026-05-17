package ma.ac.esi.gameverseacademy.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ac.esi.gameverseacademy.repository.ModRepository;
import java.io.IOException;

public class ModDeleteController extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            ModRepository repo = new ModRepository();
            repo.deleteMod(id);
        }
        response.sendRedirect(request.getContextPath() + "/mods");
    }
}