package mn.ezbuy.adminservice.repository;

import mn.ezbuy.adminservice.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RankingRepository extends JpaRepository<Ranking, Long> {

//    @Query(value = "BEGIN TRAN UPDATE TABLE WITH (SERIALIZABLE ) SET FREQUENCY = FREQUENCY + 1, TOTAL = TOTAL + :value WHERE PRODUCT_ID = :productId " +
//            "IF @@row_count = 0 BEGIN INSERT INTO RANKINGS (PRODUCT_ID,FREQUENCY,TOTAL) VALUES (:productId,1,:value) END COMMIT TRAN", nativeQuery = true)
//    void saveRanking(@Param("productId") Long productId, @Param("value") int value);

    Optional<Ranking> getRankingByProductId(Long productId);

}
