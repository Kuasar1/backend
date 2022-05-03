package mn.ezbuy.adminservice.repository;

import mn.ezbuy.adminservice.entity.Like;
import mn.ezbuy.adminservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query(value = "SELECT * FROM LIKES WHERE PRODUCT_ID = :productId AND USER_ID = :userId", nativeQuery = true)
    Optional<Like> getLikeForUser(@Param("userId") Long userId,@Param("productId") Long productId);

}
