package ma.emsi.catalogservice;

import ma.emsi.catalogservice.entities.Product;
import ma.emsi.catalogservice.enums.Category;
import ma.emsi.catalogservice.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.Random;

@SpringBootApplication
public class CatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }

    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return new RepositoryRestConfigurer() {
            @Override
            public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
                config.exposeIdsFor(Product.class);
            }
        };
    }

    @Bean
    CommandLineRunner commandLineRunner(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                Random random = new Random();

                // --- ELECTRONICS ---
                productRepository.save(createProduct("Laptop HP Envy", "Intel i7, 16GB RAM, SSD 512GB", 12000, 10, Category.ELECTRONICS, "laptop"));
                productRepository.save(createProduct("MacBook Pro M2", "Apple Silicon, 32GB Unified Memory", 25000, 5, Category.ELECTRONICS, "macbook"));
                productRepository.save(createProduct("Smartphone Samsung S23", "Galaxy S23 Ultra, 256GB", 11000, 15, Category.ELECTRONICS, "smartphone"));
                productRepository.save(createProduct("iPhone 15 Pro", "Titanium, A17 Pro Chip", 14000, 8, Category.ELECTRONICS, "iphone"));
                productRepository.save(createProduct("Sony WH-1000XM5", "Noise Cancelling Headphones", 3500, 20, Category.ELECTRONICS, "headphones"));
                productRepository.save(createProduct("Gaming Monitor", "27 inch 144Hz, 4K UHD", 4500, 12, Category.ELECTRONICS, "monitor"));
                productRepository.save(createProduct("Logitech MX Master", "Wireless Mouse Ergonomic", 900, 30, Category.ELECTRONICS, "mouse"));

                // --- CLOTHING ---
                productRepository.save(createProduct("Men's T-Shirt", "Cotton Black, Size M", 250, 50, Category.CLOTHING, "tshirt"));
                productRepository.save(createProduct("Denim Jacket", "Vintage Style, Blue", 650, 20, Category.CLOTHING, "jacket"));
                productRepository.save(createProduct("Running Shoes", "Air Zoom Pegasus", 1200, 15, Category.CLOTHING, "sneakers"));
                productRepository.save(createProduct("Summer Dress", "Floral Pattern, Light", 450, 25, Category.CLOTHING, "dress"));
                productRepository.save(createProduct("Hoodie Grey", "Cotton Fleece, Comfortable", 500, 40, Category.CLOTHING, "hoodie"));
                productRepository.save(createProduct("Leather Belt", "Genuine Leather, Brown", 300, 35, Category.CLOTHING, "belt"));

                // --- HOME ---
                productRepository.save(createProduct("Sofa Couch", "Green Velvet, 3 Seater", 8000, 4, Category.HOME, "sofa"));
                productRepository.save(createProduct("Wooden Table", "Oak Wood Dining Table", 4500, 6, Category.HOME, "table"));
                productRepository.save(createProduct("Desk Lamp", "LED Adjustable Light", 300, 40, Category.HOME, "lamp"));
                productRepository.save(createProduct("King Size Bed", "Memory Foam Mattress Included", 12000, 3, Category.HOME, "bed"));
                productRepository.save(createProduct("Ceramic Vase", "Handmade Minimalist", 150, 60, Category.HOME, "vase"));
                productRepository.save(createProduct("Coffee Maker", "Espresso Machine Automatic", 2500, 10, Category.HOME, "coffee"));

                // --- BOOKS ---
                productRepository.save(createProduct("Clean Code", "Robert C. Martin", 450, 20, Category.BOOKS, "book"));
                productRepository.save(createProduct("Harry Potter", "J.K. Rowling - Complete Set", 1200, 10, Category.BOOKS, "novel"));
                productRepository.save(createProduct("The Alchemist", "Paulo Coelho", 150, 30, Category.BOOKS, "book"));
                productRepository.save(createProduct("Java Concurrency", "Brian Goetz", 550, 15, Category.BOOKS, "programming"));
                productRepository.save(createProduct("Cooking Italian", "Traditional Recipes", 300, 25, Category.BOOKS, "cooking"));

                // --- ACCESSORIES ---
                productRepository.save(createProduct("Smart Watch", "Fitness Tracker, Black", 1500, 20, Category.ACCESSORIES, "watch"));
                productRepository.save(createProduct("Sunglasses", "UV Protection, Aviator", 800, 30, Category.ACCESSORIES, "sunglasses"));
                productRepository.save(createProduct("Leather Wallet", "Slim Bifold, RFID Block", 400, 40, Category.ACCESSORIES, "wallet"));
                productRepository.save(createProduct("Backpack", "Waterproof Laptop Bag", 700, 25, Category.ACCESSORIES, "backpack"));

                System.out.println("✅ Base de données initialisée avec succès !");
            }else{
                System.out.println("ℹ️ Les produits existent déjà, pas d'initialisation.");
            }
        };
    }

    // Méthode utilitaire pour créer un produit plus proprement
    private Product createProduct(String name, String desc, double price, int qty, Category cat, String imageTag) {
        // On utilise LoremFlickr pour avoir une image réelle basée sur un mot clé
        // L'ajout de ?random permet d'éviter que le navigateur mette en cache la même image pour tout le monde
        String imageUrl = "https://loremflickr.com/320/240/" + imageTag + "?lock=" + name.hashCode();

        return Product.builder()
                .name(name)
                .description(desc)
                .price(price)
                .quantity(qty)
                .category(cat)
                .imageUrl(imageUrl)
                .build();
    }
}
