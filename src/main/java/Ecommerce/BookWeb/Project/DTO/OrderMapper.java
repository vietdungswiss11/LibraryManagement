package Ecommerce.BookWeb.Project.DTO;

import Ecommerce.BookWeb.Project.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    
    public OrderDTO toOrderDTO(Order order) {
        if (order == null) {
            return null;
        }
        
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        if (order.getUser() != null) {
            dto.setUserDTO(toUserDTO(order.getUser()));
        }
        
        if (order.getAddress() != null) {
            dto.setAddressDTO(toAddressDTO(order.getAddress()));
        }

        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        
        // order items
        if (order.getOrderItems() != null) {
            List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                    .map(this::toOrderItemDTO)
                    .collect(Collectors.toList());
            dto.setOrderItems(itemDTOs);
            // Tính tổng tiền
            double subTotal = itemDTOs.stream()
                    .mapToDouble(OrderItemDTO::getTotalPrice)//price * quantity
                    .sum();
            dto.setSubtotal(subTotal);
            dto.setTotalItems(itemDTOs.size());
        }
        // Payment
        if(order.getPayment() != null) {
            dto.setPaymentDTO(toPaymentDTO(order.getPayment()));
        }

        //shipping
        if(order.getShipping() != null) {
            dto.setShippingDTO(toShippingDTO(order.getShipping()));
            dto.setShippingFee(order.getShipping().getShippingCost());
        }


        return dto;
    }
    
    public OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        
        if (orderItem.getBook() != null) {
            dto.setBook(toBookDTO(orderItem.getBook()));
        }
        
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getUnitPrice());
        
        return dto;
    }

    public BookDTO toBookDTO(Book book) {
        if (book == null) {
            return null;
        }

        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setImageUrl(book.getImages().get(0).getUrl());
        return dto;
    }

    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());

        return dto;
    }

    public AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }

        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setAddressLine(address.getAddressLine());
        dto.setPhoneNumber(address.getPhoneNumber());
        dto.setRecipientName(address.getRecipientName());

        return dto;
    }

    public PaymentInfoDTO toPaymentDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentInfoDTO dto = new PaymentInfoDTO();
        dto.setId(payment.getId());
        dto.setPaymentMethod(payment.getPaymentMethod());
        if(dto.getPaymentMethod().equals("VNPAY")) {

        }
        dto.setStatus(payment.getStatus());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        return dto;
    }

    public ShippingInfoDTO toShippingDTO(Shipping shipping) {
        if (shipping == null) {
            return null;
        }

        ShippingInfoDTO dto = new ShippingInfoDTO();
        dto.setId(shipping.getId());
        dto.setShippingCode(shipping.getShippingCode());
        dto.setShippingProvider(shipping.getShippingProvider());
        dto.setStatus(shipping.getStatus());
        dto.setEstimatedDelivery(shipping.getEstimatedDelivery());
        dto.setActualDelivery(shipping.getActualDelivery());
        dto.setShippingFee(shipping.getShippingCost());
        return dto;
    }
}
