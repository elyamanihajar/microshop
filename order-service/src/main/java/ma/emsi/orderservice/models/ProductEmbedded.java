// Fichier: ProductEmbedded.java
package ma.emsi.orderservice.models;

import lombok.Data;
import java.util.List;

@Data
public class ProductEmbedded {
    private List<Product> products;
}