package ma.ac.esi.gameverseacademy.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.service.UserService;
import java.io.IOException;

@WebServlet("/LoginController")
public class LoginController extends HttpServlet {

    // Ajout de la méthode doGet pour éviter l'erreur 405
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirige vers la page de login (index.html) si on accède à l'URL directement
        response.sendRedirect(request.getContextPath() + "/index.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("uname");
        String password = request.getParameter("psw");
        
        UserService userService = new UserService();
        User user = userService.findUserByCredentials(login, password);

        if (user != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user); 
            // Redirection vers la liste des mods après succès [cite: 44]
            response.sendRedirect(request.getContextPath() + "/mods");
        } else {
            // Redirection vers une page d'erreur en cas d'échec
            response.sendRedirect(request.getContextPath() + "/error.html");
        }
    }
}