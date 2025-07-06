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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Lấy tất cả đơn hàng của một user
    @GetMapping("/user/{userId}")
    public List<OrderDTO> getOrdersByUserId(@PathVariable int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        List<OrderDTO> orderDTOs = orderRepository.findByUser(user).stream()
                .map(order -> orderMapper.toOrderDTO(order))
                .collect(Collectors.toList());
        return orderDTOs;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable int id) {
        return orderRepository.findById(id)
                .map(orderMapper::toOrderDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequestDTO createOrderRequest) {
        Order order = orderService.createOrderFromDTO(createOrderRequest);
        return ResponseEntity.ok(orderMapper.toOrderDTO(order));
    }




}
