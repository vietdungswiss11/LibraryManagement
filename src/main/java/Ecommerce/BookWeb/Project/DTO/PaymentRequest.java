package Ecommerce.BookWeb.Project.DTO;

import lombok.Data;

@Data
public class PaymentRequest {
    private int id;
    private String orderNumber;
    private double amount;
    private String orderInfor;
    private String urlReturn;
}
