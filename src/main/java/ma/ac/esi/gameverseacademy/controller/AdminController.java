package ma.ac.esi.gameverseacademy.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ma.ac.esi.gameverseacademy.util.SecurityUtils;
import java.io.IOException;

@WebServlet("/admin")
public class AdminController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SecurityUtils.checkAccess(request, response, "ADMIN")) return;
        request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);
    }
}