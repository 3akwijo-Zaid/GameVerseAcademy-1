package ma.ac.esi.gameverseacademy.service;

import ma.ac.esi.gameverseacademy.model.Payment;
import ma.ac.esi.gameverseacademy.repository.PaymentRepository;
import java.util.List;

public class PaymentService {
    private PaymentRepository paymentRepository = new PaymentRepository();

    public List<Payment> getAllPayments() {
        return paymentRepository.getAllPayments();
    }

    public int addPayment(Payment payment) {
        return paymentRepository.addPayment(payment);
    }
}
