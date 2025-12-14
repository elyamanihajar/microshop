package ma.emsi.searchservice.feign;

import ma.emsi.searchservice.models.Product;
import ma.emsi.searchservice.models.ProductResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "catalog-service")
public interface ProductRestClient {

    @GetMapping("/products/{id}")
    Mono<Product> getProductById(@PathVariable("id") Long id);

    @GetMapping("/products")
    Mono<ProductResponse> getAllProducts();
    // On reçoit UN objet réponse (Mono) de manière asynchrone
}