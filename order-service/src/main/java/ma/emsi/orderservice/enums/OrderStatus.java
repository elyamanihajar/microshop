package ma.emsi.orderservice.enums;

public enum OrderStatus {
    PENDING,    // En attente de validation
    CONFIRMED,  // Validée par le magasin
    DELIVERED,  // Livrée au client
    CANCELED    // Annulée
}