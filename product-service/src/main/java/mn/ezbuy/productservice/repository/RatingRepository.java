package mn.ezbuy.productservice.repository;

import mn.ezbuy.productservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query(value = "SELECT * FROM RATINGS WHERE PRODUCT_ID = :productId AND USER_ID = :userId", nativeQuery = true)
    Optional<Rating> getRatingForUser(@Param("userId") Long userId, @Param("productId") Long productId);

}
