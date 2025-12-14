package ma.emsi.orderservice;

import ma.emsi.orderservice.entities.Order;
import ma.emsi.orderservice.entities.ProductItem;
import ma.emsi.orderservice.enums.OrderStatus;
import ma.emsi.orderservice.models.Product;
import ma.emsi.orderservice.repositories.OrderRepository;
import ma.emsi.orderservice.repositories.ProductItemRepository;
import ma.emsi.orderservice.services.InventoryRestClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(OrderRepository orderRepository,
                            ProductItemRepository productItemRepository,
                            InventoryRestClient inventoryRestClient) {
        return args -> {
            if (orderRepository.count() == 0) {
            // 1. Récupération des produits
            List<Product> allProducts = inventoryRestClient.getAllProducts().getEmbedded().getProducts();

            // 2. Création des commandes
            for (int i = 0; i < 3; i++) {

                Order order = Order.builder()
                        .customerId(1L + i)
                        .createdAt(new Date())
                        .status(OrderStatus.PENDING)
                        .total(0) // Initialisation à 0
                        .build();

                Order savedOrder = orderRepository.save(order);

                double calculatedTotalOrder = 0; // Variable temporaire pour sommer le montant

                for (Product p : allProducts) {
                    if (Math.random() > 0.5) {
                        // Vérification stock
                        if (p.getQuantity() <= 0) continue;

                        int quantityWished = new Random().nextInt(10) + 1;
                        int quantityFinal = Math.min(quantityWished, p.getQuantity());

                        // On calcule le prix total de la ligne (Sous-total)
                        double productItemPrice = p.getPrice() * quantityFinal;

                        ProductItem item = ProductItem.builder()
                                .productId(p.getId())
                                .order(savedOrder)
                                .quantity(quantityFinal)
                                .subTotal(productItemPrice)
                                .build();

                        productItemRepository.save(item);

                        // Sommation du total
                        calculatedTotalOrder += productItemPrice;
                    }
                }

                // MISE A JOUR DE LA COMMANDE AVEC LE TOTAL FINAL
                savedOrder.setTotal(calculatedTotalOrder);
                orderRepository.save(savedOrder); // Update SQL
            }
            System.out.println("✅ Commandes générées avec le montant total stocké en BDD !");}
            else{
                System.out.println("ℹ️ Des commandes existent déjà.");
            }
        };
    }
}