package ma.emsi.recommandationservice.web;

import lombok.RequiredArgsConstructor;
import ma.emsi.recommandationservice.feign.CatalogClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final CatalogClient catalogClient;

    @GetMapping("/similar/{productId}")
    public List<?> getSimilarProducts(@PathVariable Long productId) {
        try {
            // 1. Récupérer le produit cible
            var product = catalogClient.getProduct(productId);
            if (product == null || product.getCategory() == null) {
                return Collections.emptyList();
            }

            // 2. Récupérer la réponse wrapper
            var response = catalogClient.getSimilarProducts(product.getCategory(), productId);

            // 3. Extraire la liste proprement
            // Grâce à @Data dans CatalogClient.java, .getEmbedded() existe maintenant
            if (response != null && response.getEmbedded() != null) {
                return response.getEmbedded().getProducts();
            }

            return Collections.emptyList();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}