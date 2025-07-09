package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.DTO.AddressDTO;
import Ecommerce.BookWeb.Project.DTO.CreateOrderRequestDTO;
import Ecommerce.BookWeb.Project.DTO.OrderDTO;
import Ecommerce.BookWeb.Project.DTO.OrderMapper;
import Ecommerce.BookWeb.Project.Model.Order;
import Ecommerce.BookWeb.Project.Model.User;
import Ecommerce.BookWeb.Project.Repository.OrderRepository;
import Ecommerce.BookWeb.Project.Repository.UserRepository;
import Ecommerce.BookWeb.Project.Service.OrderService;
import Ecommerce.BookWeb.Project.Service.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VNPAYService vnPayService;

    // Lấy tất cả đơn hàng của một user có phân trang
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getOrdersByUserId(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Không tìm thấy người dùng với ID: " + userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Tạo đối tượng phân trang
            Pageable paging = PageRequest.of(
                    page,
                    size,
                    sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
            );

            // Lấy danh sách đơn hàng của user có phân trang
            Page<Order> pageOrders = orderRepository.findByUser(user, paging);

            // Chuyển đổi sang DTO
            List<OrderDTO> orderDTOs = pageOrders.getContent().stream()
                    .map(orderMapper::toOrderDTO)
                    .collect(Collectors.toList());

            // Tạo response
            Map<String, Object> response = new HashMap<>();
            response.put("orders", orderDTOs);
            response.put("currentPage", pageOrders.getNumber());
            response.put("totalItems", pageOrders.getTotalElements());
            response.put("totalPages", pageOrders.getTotalPages());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable int id) {
        return orderRepository.findById(id)
                .map(orderMapper::toOrderDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-number/{orderNumber}")
    public ResponseEntity<OrderDTO> getOrderByOrderNumber(@PathVariable String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(orderMapper::toOrderDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
// tạo order cod
//    @PostMapping
//    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequestDTO createOrderRequest) {
//        Order order = orderService.createOrderFromDTO(createOrderRequest);
//        return ResponseEntity.ok(orderMapper.toOrderDTO(order));
//    }

    // tạo order nâng cấp có VNPAY
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequestDTO createOrderRequest, HttpServletRequest request) {
        Order order = orderService.createOrderFromDTO(createOrderRequest);

        // Nếu là VNPAY thì tạo link thanh toán
        String paymentUrl = null;
        if ("vnpay".equalsIgnoreCase(order.getPayment().getPaymentMethod())) {
            // orderInfo là mô tả đơn hàng
            String orderInfo = "Thanh toan don hang " + order.getOrderNumber();
            paymentUrl = vnPayService.createOrder(request, order.getTotalAmount(), orderInfo, order.getOrderNumber());
        }

        // Map sang DTO như cũ
        OrderDTO dto = orderMapper.toOrderDTO(order);
        // Thêm trường paymentUrl vào DTO (nếu là VNPAY)
        if (paymentUrl != null) {
            // Nếu OrderDTO chưa có trường này, có thể dùng Map hoặc custom DTO
            Map<String, Object> result = new HashMap<>();
            result.put("order", dto);
            result.put("paymentUrl", paymentUrl);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.ok(dto);
        }
    }

}
