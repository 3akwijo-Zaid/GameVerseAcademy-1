package ma.ac.esi.gameverseacademy.controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import ma.ac.esi.gameverseacademy.model.Rating;
import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.service.RatingService;
import ma.ac.esi.gameverseacademy.util.SecurityUtils;
import java.io.IOException;
public class RatingController extends HttpServlet {
    private RatingService ratingService = new RatingService();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SecurityUtils.checkAccess(request, response, null)) return;
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        int modId = Integer.parseInt(request.getParameter("modId"));
        int stars = Integer.parseInt(request.getParameter("stars"));
        String comment = request.getParameter("comment");
        Rating r = new Rating();
        r.setModId(modId);
        r.setUserLogin(user.getLogin());
        r.setStars(stars);
        r.setComment(comment);
        ratingService.addOrUpdateRating(r);
        response.sendRedirect(request.getContextPath() + "/mods");
    }
}
