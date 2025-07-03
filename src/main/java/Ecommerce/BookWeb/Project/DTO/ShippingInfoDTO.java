package Ecommerce.BookWeb.Project.DTO;

import Ecommerce.BookWeb.Project.Model.Shipping;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShippingInfoDTO {
    private int id;
    private String shippingCode;           // Mã vận đơn
    private String shippingProvider;     // Loại hình vận chuyển (GHTK, GHN, ...)
    private String status;         // Trạng thái vận chuyển
    private String note;                   // Ghi chú vận chuyển
    
    // Thông tin ngày tháng
    private LocalDateTime estimatedDelivery; // Ngày dự kiến giao hàng
    private LocalDateTime actualDelivery;        // Ngày giao hàng thành công
    
    // Thông tin phí
    private double shippingFee;            // Phí vận chuyển

    
//    // Trạng thái
//    public boolean isDelivered() {
//        return status == ShippingStatus.DELIVERED;
//    }
//
//    public boolean isReturning() {
//        return status == ShippingStatus.RETURNING;
//    }
//
//    public boolean isReturned() {
//        return status == ShippingStatus.RETURNED;
//    }
}
