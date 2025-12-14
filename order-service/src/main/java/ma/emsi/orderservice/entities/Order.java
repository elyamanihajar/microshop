package ma.emsi.orderservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.emsi.orderservice.enums.OrderStatus;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders") // "order" est un mot réservé en SQL
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private double total;

    private Long customerId; // Pour lier à un futur Customer-Service

    // Un panier/commande contient plusieurs lignes de produits
    @OneToMany(mappedBy = "order")
    private List<ProductItem> productItems;
}