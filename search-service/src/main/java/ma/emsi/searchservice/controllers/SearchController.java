package ma.emsi.searchservice.controllers;

import lombok.RequiredArgsConstructor;
import ma.emsi.searchservice.feign.ProductRestClient;
import ma.emsi.searchservice.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    @Autowired
    private final ProductRestClient productRestClient;

    // --- 1. Recherche par nom ---
    @GetMapping("/name/{keyword}")
    public Flux<Product> searchByName(@PathVariable String keyword) {
        return getAllProductsStream()
                .filter(product -> product.getName() != null &&
                        product.getName().toLowerCase().contains(keyword.toLowerCase()));
    }

    // --- 2. Recherche par catégorie ---
    @GetMapping("/category/{category}")
    public Flux<Product> searchByCategory(@PathVariable String category) {
        return getAllProductsStream()
                .filter(product -> product.getCategory() != null &&
                        product.getCategory().equalsIgnoreCase(category));
    }

    // --- 3. Recherche par prix ---
    @GetMapping("/price")
    public Flux<Product> searchByPriceRange(@RequestParam double min, @RequestParam double max) {
        return getAllProductsStream()
                .filter(product -> product.getPrice() >= min && product.getPrice() <= max);
    }

    // --- 4. Test simple ---
    @GetMapping("/test")
    public String test() {
        return "test";
    }


    private Flux<Product> getAllProductsStream() {
        return productRestClient.getAllProducts()
                .flatMapMany(response -> {
                    // Vérifications défensives pour éviter les NullPointerException
                    if (response != null &&
                            response.getEmbedded() != null &&
                            response.getEmbedded().getProducts() != null) {

                        // On convertit la List<Product> en Flux<Product>
                        return Flux.fromIterable(response.getEmbedded().getProducts());
                    }
                    // Si la réponse est vide ou mal formée, on retourne un flux vide
                    return Flux.empty();
                });
    }
}