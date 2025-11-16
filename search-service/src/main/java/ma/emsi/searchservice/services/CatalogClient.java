package ma.emsi.searchservice.services;

import ma.emsi.searchservice.models.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class CatalogClient {
    private final WebClient webClient;

    public CatalogClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8880").build();
    }

    public Flux<Product> getAllProducts() {
        return webClient.get()
                .uri("/catalog-service/products")
                .retrieve()
                .bodyToFlux(Product.class);
    }
}
