package mn.ezbuy.recommendationservice.repository;

import mn.ezbuy.recommendationservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query(value = "SELECT * FROM RATINGS WHERE USER_ID = :userId ORDER BY RATING", nativeQuery = true)
    List<Rating> getTopRatingsForUser(@Param("userId")  Long userId);

}
