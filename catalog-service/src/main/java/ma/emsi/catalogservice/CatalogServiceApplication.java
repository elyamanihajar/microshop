package ma.emsi.catalogservice;

import ma.emsi.catalogservice.entities.Product;
import ma.emsi.catalogservice.enums.Category;
import ma.emsi.catalogservice.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(ProductRepository productRepository) {
        return args -> {
            productRepository.save(Product.builder()
                    .name("Laptop HP")
                    .description("16 GB Ram")
                    .price(12000)
                    .quantity(5)
                    .category(Category.ELECTRONICS)
                    .build());
            productRepository.save(Product.builder()
                    .name("Smartphone Samsung")
                    .description("Galaxy S23")
                    .price(9000)
                    .quantity(15)
                    .category(Category.ELECTRONICS)
                    .build());
            productRepository.save(Product.builder()
                    .name("Sofa Couch")
                    .description("Color Green")
                    .price(14000)
                    .quantity(10)
                    .category(Category.HOME)
                    .build());
            productRepository.save(Product.builder()
                    .name("T-shirt")
                    .description("Color:Black | Size:M")
                    .price(400)
                    .quantity(25)
                    .category(Category.CLOTHING)
                    .build());
            productRepository.findAll().forEach(product -> {
                System.out.println("---------------");
                System.out.println(product.getId());
                System.out.println(product.getName());
                System.out.println(product.getDescription());
                System.out.println(product.getCategory());
                System.out.println(product.getPrice());
                System.out.println(product.getQuantity());
            });
        };
    }

}
