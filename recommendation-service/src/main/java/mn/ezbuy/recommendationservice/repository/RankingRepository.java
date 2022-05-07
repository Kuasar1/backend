package mn.ezbuy.recommendationservice.repository;

import mn.ezbuy.recommendationservice.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> getRankingByProductId(Long productId);

}
