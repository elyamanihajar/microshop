package ma.emsi.searchservice.models;

import lombok.Data;

import java.util.List;
@Data
public class ProductEmbedded {
    private List<Product> products;
}
