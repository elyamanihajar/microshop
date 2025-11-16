package ma.emsi.searchservice.models;

import lombok.Data;
import ma.emsi.searchservice.enums.Category;

@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private Category category;
}
