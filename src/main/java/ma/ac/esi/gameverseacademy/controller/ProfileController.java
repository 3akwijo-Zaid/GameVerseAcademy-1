package ma.ac.esi.gameverseacademy.controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.repository.GameCardRepository;
import ma.ac.esi.gameverseacademy.repository.UserRepository;
import ma.ac.esi.gameverseacademy.util.SecurityUtils;
import java.io.IOException;
public class ProfileController extends HttpServlet {
    private UserRepository userRepo = new UserRepository();
    private GameCardRepository cardRepo = new GameCardRepository();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SecurityUtils.checkAccess(request, response, null)) return;
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        request.setAttribute("myCards", cardRepo.getCardsByUser(user.getLogin()));
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SecurityUtils.checkAccess(request, response, null)) return;
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirm = request.getParameter("confirmPassword");
        User check = userRepo.getUserByCredentials(user.getLogin(), currentPassword);
        if (check == null) {
            request.setAttribute("error", "Mot de passe actuel incorrect.");
        } else if (!newPassword.equals(confirm)) {
            request.setAttribute("error", "Les mots de passe ne correspondent pas.");
        } else if (newPassword.length() < 4) {
            request.setAttribute("error", "Mot de passe trop court (min 4 caracteres).");
        } else {
            userRepo.updatePassword(user.getLogin(), newPassword);
            request.setAttribute("success", "Mot de passe mis a jour avec succes !");
        }
        request.setAttribute("myCards", cardRepo.getCardsByUser(user.getLogin()));
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }
}
