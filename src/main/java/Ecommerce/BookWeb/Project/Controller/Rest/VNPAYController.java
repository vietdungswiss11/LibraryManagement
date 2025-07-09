package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.DTO.PaymentRequest;
import Ecommerce.BookWeb.Project.Service.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vnpay")
public class VNPAYController {
    @Autowired
    private VNPAYService vnPayService;

    // Tạo order, trả về URL thanh toán cho FE
//    @PostMapping("/create-payment")
//    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest req, HttpServletRequest request) {
//        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
//        String vnpayUrl = vnPayService.createOrder(request, req.getAmount(), req.getOrderInfor(), baseUrl);
//        return ResponseEntity.ok(Collections.singletonMap("paymentUrl", vnpayUrl));
//    }

    // Xử lý callback từ VNPAY
    @GetMapping("/vnpay-payment-return")
    public ResponseEntity<?> paymentReturn(HttpServletRequest request) {
        int paymentStatus = vnPayService.orderReturn(request);
        // Lấy các tham số cần thiết
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderInfo);
        result.put("totalPrice", totalPrice);
        result.put("paymentTime", paymentTime);
        result.put("transactionId", transactionId);
        result.put("status", paymentStatus == 1 ? "success" : "fail");

        return ResponseEntity.ok(result);
    }
}
