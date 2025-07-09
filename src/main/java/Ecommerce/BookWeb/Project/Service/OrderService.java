package Ecommerce.BookWeb.Project.Service;

import Ecommerce.BookWeb.Project.DTO.CreateOrderRequestDTO;
import Ecommerce.BookWeb.Project.DTO.OrderItemDTO;
import Ecommerce.BookWeb.Project.Model.*;
import Ecommerce.BookWeb.Project.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ShippingRepository shippingRepository;
    @Autowired
    private OrderRepository orderRepository;

    public Order createOrderFromDTO(CreateOrderRequestDTO dto) {
        // 1. Lấy thông tin người dùng và địa chỉ
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findById(dto.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // 2. Tạo thanh toán (Payment)
        Payment payment = new Payment();
        payment.setPaymentMethod(dto.getPaymentMethod());
        if(payment.getPaymentMethod().equals("vnpay")){

        }
        payment.setStatus("PENDING");
        payment.setAmount(0); // tạm thời set 0, sẽ cập nhật sau
        payment.setTransactionId(null);
        payment.setPaymentDate(null);
        paymentRepository.save(payment);

        // 3. Tạo thông tin vận chuyển (Shipping)
        Shipping shipping = new Shipping();
        shipping.setShippingProvider(dto.getShippingProvider());
        shipping.setShippingCode(UUID.randomUUID().toString()); // random mã đơn vận
        shipping.setStatus("PENDING");
        shipping.setShippingCost(15000); // hardcode tạm phí vận chuyển
        shipping.setRecipientName(address.getRecipientName());
        shipping.setPhoneNumber(address.getPhoneNumber());
        shipping.setAddressLine(address.getAddressLine());
        shippingRepository.save(shipping);

        // 4. Tạo Order và OrderItem
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setAddress(address);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setPayment(payment);
        order.setShipping(shipping);

        List<OrderItem> orderItems = new ArrayList<>();
        double subTotal = 0;

        for (CreateOrderRequestDTO.OrderItemRequestDTO itemDTO : dto.getOrderItems()) {
            Book book = bookRepository.findById(itemDTO.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            OrderItem item = new OrderItem();
            item.setBook(book);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(book.getDiscountPrice());
            item.setOrder(order);

            subTotal += item.getUnitPrice() * item.getQuantity();
            orderItems.add(item);
        }

        order.setOrderItems(orderItems);

        double shippingFee = shipping.getShippingCost();
        double totalAmount = subTotal + shippingFee;
        order.setTotalAmount(totalAmount);

        // Cập nhật lại số tiền thanh toán
        payment.setAmount(totalAmount);
        paymentRepository.save(payment);

        // 5. Lưu Order (cùng với orderItems cascade)
        return orderRepository.save(order);
    }

    //generate order number random
    public String generateOrderNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMdd");
        String datePart = LocalDate.now().format(formatter);
        String randomPart = String.format("%03d", new Random().nextInt(1000));
        return "ORD" + datePart + "-" + randomPart;
    }
}

