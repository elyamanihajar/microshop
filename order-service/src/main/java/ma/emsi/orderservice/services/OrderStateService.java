package ma.emsi.orderservice.services;

import lombok.RequiredArgsConstructor;
import ma.emsi.orderservice.entities.Order;
import ma.emsi.orderservice.enums.OrderStatus;
import ma.emsi.orderservice.repositories.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStateService {

    private final OrderRepository orderRepository;

    // S'exécute toutes les 30 secondes pour la démo (au lieu de 5 min pour tester plus vite)
    // Pour 5 minutes réelles, mettez fixedRate = 300000
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void autoDeliverOrders() {
        // Récupérer toutes les commandes qui sont CONFIRMED
        // Note: Idéalement, il faudrait faire une requête SQL custom "findByStatusAndUpdatedAtBefore"
        // Mais pour l'exemple simple, on filtre en Java.
        List<Order> confirmedOrders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
                .toList();

        confirmedOrders.forEach(order -> {
            // Simulation : On considère qu'après 30 sec (ou 5 min) la livraison est faite
            // Ici, on les passe simplement à DELIVERED automatiquement.
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
            System.out.println("🚚 Commande #" + order.getId() + " a été livrée automatiquement !");
        });
    }
}