package ma.emsi.catalogservice.web;

import lombok.RequiredArgsConstructor;
import ma.emsi.catalogservice.entities.Product;
import ma.emsi.catalogservice.repositories.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products") // On étend l'API existante
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductRepository productRepository;

    // Endpoint: POST /products/{id}/decrement?qty=2
    @PostMapping("/{id}/decrement")
    @Transactional // Important pour garantir la cohérence (Lecture + Ecriture)
    public ResponseEntity<Void> decrementStock(@PathVariable Long id, @RequestParam int qty) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        if (product.getQuantity() < qty) {
            throw new RuntimeException("Stock insuffisant pour le produit : " + product.getName());
        }

        // Mise à jour du stock
        product.setQuantity(product.getQuantity() - qty);
        productRepository.save(product);

        return ResponseEntity.ok().build();
    }
}