package ma.emsi.orderservice.services;

import ma.emsi.orderservice.models.PagedProductResponse;
import ma.emsi.orderservice.models.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "catalog-service")
public interface InventoryRestClient {

    // Appel GET http://catalog-service/products/{id}
    // Note: Comme catalog-service utilise Spring Data REST, l'URL standard est /products/{id}
    @GetMapping("/products/{id}")
    Product findProductById(@PathVariable("id") Long id);

    @GetMapping("/products")
    PagedProductResponse getAllProducts();

    @PostMapping("/products/{id}/decrement")
    void decrementProductQuantity(@PathVariable("id") Long id, @RequestParam("qty") int qty);
}
