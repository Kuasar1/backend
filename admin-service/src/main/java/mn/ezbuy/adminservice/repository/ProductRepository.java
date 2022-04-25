package mn.ezbuy.adminservice.repository;

import mn.ezbuy.adminservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findProductByTitle(String title);

    @Query(value = "SELECT * FROM PRODUCTS WHERE PRODUCTS.CATEGORIES LIKE %:category%", nativeQuery = true)
    List<Product> findByCategory(@Param("category") String category);

    @Query(value = "SELECT * FROM PRODUCTS WHERE PRODUCTS.TITLE LIKE %:name%", nativeQuery = true)
    List<Product> findByTitle(@Param("name") String name);

}
