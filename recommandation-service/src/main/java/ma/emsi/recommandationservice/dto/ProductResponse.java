package ma.emsi.recommandationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class ProductResponse {
    @JsonProperty("_embedded")
    private ProductEmbedded embedded;

}
