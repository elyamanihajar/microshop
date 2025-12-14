package ma.emsi.catalogservice.repositories;

import ma.emsi.catalogservice.entities.Product;
import ma.emsi.catalogservice.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.awt.print.Pageable;
import java.util.List;

@RepositoryRestResource
public interface ProductRepository  extends JpaRepository<Product, Long> {
    // Trouver 4 produits de la même catégorie, mais exclure l'ID actuel (pour ne pas se recommander soi-même)
    List<Product> findTop4ByCategoryAndIdNot(@Param("category") Category category,@Param("id") Long id);
}
