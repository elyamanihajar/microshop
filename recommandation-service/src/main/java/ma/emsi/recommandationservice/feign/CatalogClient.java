package ma.emsi.recommandationservice.feign;

import ma.emsi.recommandationservice.dto.ProductDTO;
import ma.emsi.recommandationservice.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// On appelle le service CATALOG-SERVICE
@FeignClient(name = "catalog-service")
public interface CatalogClient {

    // Récupérer un produit spécifique
    @GetMapping("/products/{id}")
    ProductDTO getProduct(@PathVariable Long id);

    // Récupérer les produits similaires (appel à notre nouvelle méthode repository via l'API REST auto-générée)
    // Note: Spring Data REST expose les méthodes de recherche sous /search/...
    @GetMapping("/products/search/findTop4ByCategoryAndIdNot")
    ProductResponse getSimilarProducts(@RequestParam("category") String category, @RequestParam("id") Long id);
}
