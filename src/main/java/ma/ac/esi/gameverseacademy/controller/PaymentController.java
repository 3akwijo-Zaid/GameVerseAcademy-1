package ma.ac.esi.gameverseacademy.controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import ma.ac.esi.gameverseacademy.model.Payment;
import ma.ac.esi.gameverseacademy.service.PaymentService;
import ma.ac.esi.gameverseacademy.util.SecurityUtils;
import java.io.IOException;
import java.util.List;
public class PaymentController extends HttpServlet {
    private PaymentService paymentService = new PaymentService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SecurityUtils.checkAccess(request, response, "ADMIN")) return;
        List<Payment> payments = paymentService.getAllPayments();
        request.setAttribute("payments", payments);
        request.getRequestDispatcher("/WEB-INF/views/payments.jsp").forward(request, response);
    }
}
