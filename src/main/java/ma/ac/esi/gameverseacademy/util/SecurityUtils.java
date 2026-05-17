package ma.ac.esi.gameverseacademy.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ac.esi.gameverseacademy.model.User;
import java.io.IOException;

public class SecurityUtils {

    public static boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null && session.getAttribute("user") != null);
    }

    public static boolean checkAccess(HttpServletRequest request, HttpServletResponse response, String requiredRole) throws IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return false;
        }

        if (requiredRole != null) {
            User user = (User) session.getAttribute("user");
            if (!requiredRole.equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/error403.html");
                return false;
            }
        }
        return true;
    }
}