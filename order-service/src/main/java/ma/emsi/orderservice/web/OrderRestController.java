package ma.emsi.orderservice.web;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import ma.emsi.orderservice.entities.Order;
import ma.emsi.orderservice.entities.ProductItem;
import ma.emsi.orderservice.enums.OrderStatus;
import ma.emsi.orderservice.models.OrderRequest;
import ma.emsi.orderservice.models.Product;
import ma.emsi.orderservice.repositories.OrderRepository;
import ma.emsi.orderservice.repositories.ProductItemRepository;
import ma.emsi.orderservice.services.InventoryRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderRepository orderRepository;
    private final InventoryRestClient inventoryRestClient;
    private final ProductItemRepository productItemRepository;
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }


    @GetMapping("/orders")
    public List<Order> allOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/fullOrder/{id}")
    public Order getOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);

        if (order != null) {
            // Pour chaque ligne de produit dans la commande
            order.getProductItems().forEach(pi -> {
                // Récupérer les détails depuis le catalogue via Feign
                try {
                    pi.setProduct(inventoryRestClient.findProductById(pi.getProductId()));
                } catch (Exception e) {
                    // Gérer le cas où le produit n'existe plus ou le service est down
                    pi.setProduct(null);
                }
            });
        }

        return order;
    }

    @PostMapping("/orders")
    @Transactional // Important pour garantir que tout est sauvegardé ou rien
    public Order placeOrder(@RequestBody OrderRequest request) {
        // --- ETAPE 1 : VERIFICATION PREALABLE DU STOCK ---
        // Avant de créer la commande, on vérifie que TOUS les produits sont disponibles
        for (OrderRequest.OrderLineItem itemRequest : request.getItems()) {
            // Appel au Catalog-Service pour avoir l'état actuel du produit
            Product productInCatalog = inventoryRestClient.findProductById(itemRequest.getProductId());

            if (productInCatalog == null) {
                throw new RuntimeException("Produit introuvable : ID " + itemRequest.getProductId());
            }

            if (productInCatalog.getQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Stock insuffisant pour le produit : "
                        + productInCatalog.getName()
                        + " (Demandé: " + itemRequest.getQuantity()
                        + ", Disponible: " + productInCatalog.getQuantity() + ")");
            }
        }

        // --- ETAPE 2 : CREATION DE LA COMMANDE (Si tout est OK) ---
        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .createdAt(new Date())
                .status(OrderStatus.PENDING)
                .total(0)
                .build();

        Order savedOrder = orderRepository.save(order);

        double totalAmount = 0;

        for (OrderRequest.OrderLineItem itemRequest : request.getItems()) {
            // On pourrait refaire un appel pour avoir le prix à jour,
            // ou faire confiance au prix envoyé par le front (moins sécurisé),
            // ou réutiliser le produit récupéré plus haut (optimisation possible).
            // Pour faire simple ici, on reprend le prix envoyé ou on le récupère.

            // Bonnes pratique : Récupérer le vrai prix du catalogue pour éviter la fraude
            Product productInCatalog = inventoryRestClient.findProductById(itemRequest.getProductId());

            double lineTotal = productInCatalog.getPrice() * itemRequest.getQuantity();

            ProductItem item = ProductItem.builder()
                    .productId(itemRequest.getProductId())
                    .quantity(itemRequest.getQuantity())
                    .subTotal(lineTotal)
                    .order(savedOrder)
                    .build();

            productItemRepository.save(item);
            totalAmount += lineTotal;
        }

        savedOrder.setTotal(totalAmount);
        return orderRepository.save(savedOrder);

    }

    // Endpoint pour changer le statut (ex: /orders/1/status?status=CONFIRMED)
    @PatchMapping("/orders/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Commande introuvable"));

        // Ici, on pourrait ajouter des règles métier (ex: impossible de passer de CANCELED à PENDING)
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
            throw new RuntimeException("Impossible de modifier une commande déjà terminée");
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }

    @PostMapping("/orders/{id}/payment-intent")
    public Map<String, String> createPaymentIntent(@PathVariable Long id) throws Exception {
        Order order = orderRepository.findById(id).orElseThrow();

        // Stripe fonctionne en CENTIMES (ex: 100 MAD = 10000 cents)
        // On multiplie par 100 et on convertit en Long
        long amountInCents = (long) (order.getTotal() * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("mad") // Devise (marche en mode test)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        // Appel aux serveurs de Stripe
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // On renvoie le clientSecret au frontend (c'est le sésame pour payer)
        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        return response;
    }

    @PostMapping("/orders/{id}/pay")
    public Order payOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow();

        // On ne traite que si la commande n'est pas déjà payée
        if (order.getStatus() == OrderStatus.PENDING) {

            // 1. Passer la commande en CONFIRMED
            order.setStatus(OrderStatus.CONFIRMED);

            // 2. DECREMENTER LE STOCK POUR CHAQUE ARTICLE
            order.getProductItems().forEach(pi -> {
                try {
                    inventoryRestClient.decrementProductQuantity(pi.getProductId(), pi.getQuantity());
                } catch (Exception e) {
                    // En production, il faudrait gérer le rollback ici (Saga pattern)
                    // Pour ce projet, on loggue juste l'erreur
                    System.err.println("Erreur décrémentation produit " + pi.getProductId());
                }
            });

            return orderRepository.save(order);
        }
        return order;
    }
}