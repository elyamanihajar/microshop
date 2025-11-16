package ma.emsi.searchservice.feign;

import ma.emsi.searchservice.models.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service")
public interface ProductRestClient {

    @GetMapping("/products/{id}")
    Product getProductById(@PathVariable Long id);

    @GetMapping("/products")
    PagedModel<Product> getAllProducts();
}
