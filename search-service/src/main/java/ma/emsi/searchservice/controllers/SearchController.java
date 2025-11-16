package ma.emsi.searchservice.controllers;

import lombok.RequiredArgsConstructor;
import ma.emsi.searchservice.feign.ProductRestClient;
import ma.emsi.searchservice.models.Product;
import ma.emsi.searchservice.services.CatalogClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final CatalogClient catalogClient;
    private ProductRestClient productRestClient;

    @GetMapping("/name/{keyword}")
    public Flux<Product> searchByName(@PathVariable String keyword) {
        return catalogClient.getAllProducts()
                .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()));
    }

    @GetMapping("/category/{category}")
    public Flux<Product> searchByCategory(@PathVariable String category) {
        return catalogClient.getAllProducts()
                .filter(p -> p.getCategory().equalsIgnoreCase(category));
    }

    @GetMapping("/price")
    public Flux<Product> searchByPrice(
            @RequestParam double min,
            @RequestParam double max
    ) {
        return catalogClient.getAllProducts()
                .filter(p -> p.getPrice() >= min && p.getPrice() <= max);
    }

    @GetMapping("/test")
    public String test(){
        return "Search service OK";
    }
}
