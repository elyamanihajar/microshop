// Fichier: PagedProductResponse.java
package ma.emsi.orderservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PagedProductResponse {
    @JsonProperty("_embedded")
    private ProductEmbedded embedded;
}