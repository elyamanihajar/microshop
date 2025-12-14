package ma.emsi.searchservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class ProductEmbedded {
    @JsonProperty("products")
    private List<Product> products;
}
