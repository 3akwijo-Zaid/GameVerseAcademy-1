package ma.ac.esi.gameverseacademy.controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import ma.ac.esi.gameverseacademy.repository.*;
import ma.ac.esi.gameverseacademy.util.SecurityUtils;
import java.io.IOException;
import java.util.List;
public class DashboardController extends HttpServlet {
    private ModRepository modRepo = new ModRepository();
    private GameCardRepository cardRepo = new GameCardRepository();
    private PaymentRepository payRepo = new PaymentRepository();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SecurityUtils.checkAccess(request, response, "ADMIN")) return;
        int totalMods = modRepo.getAllMods().size();
        int totalSales = cardRepo.getAllCards().size();
        double totalRevenue = payRepo.getAllPayments().stream().mapToDouble(p -> p.getAmount()).sum();
        long totalDownloads = modRepo.getAllMods().stream().mapToLong(m -> m.getDownloads()).sum();
        request.setAttribute("totalMods", totalMods);
        request.setAttribute("totalSales", totalSales);
        request.setAttribute("totalRevenue", String.format("%.2f", totalRevenue));
        request.setAttribute("totalDownloads", String.format("%,d", totalDownloads));
        request.setAttribute("recentMods", modRepo.getAllMods().subList(0, Math.min(5, modRepo.getAllMods().size())));
        request.setAttribute("recentPayments", payRepo.getAllPayments().subList(0, Math.min(5, payRepo.getAllPayments().size())));
        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }
}
