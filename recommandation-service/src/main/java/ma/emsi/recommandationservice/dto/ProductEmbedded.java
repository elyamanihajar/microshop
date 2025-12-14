package ma.emsi.recommandationservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductEmbedded {
    private List<ProductDTO> products;
}
