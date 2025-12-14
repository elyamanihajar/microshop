package ma.emsi.orderservice.models;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long customerId;
    private List<OrderLineItem> items;

    @Data
    public static class OrderLineItem {
        private Long productId;
        private int quantity;
        private double price; // Le prix vu par le client au moment de l'achat
    }
}
