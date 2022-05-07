package mn.ezbuy.recommendationservice.controller;

import lombok.RequiredArgsConstructor;
import mn.ezbuy.recommendationservice.entity.Like;
import mn.ezbuy.recommendationservice.entity.Rating;
import mn.ezbuy.recommendationservice.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Component
@RequestMapping("/recommendations")
public class RecommendationControllerImplementation implements RecommendationController {

    @Autowired
    private final RecommendationService recommendationService;

    @Override
    public ResponseEntity<?> getForRecommendation(String recommendationType, Long userId, String token) {
        return recommendationService.recommend(recommendationType,userId,token);
    }

    @Override
    public ResponseEntity<?> handleLike(Like request, String token) {
        return recommendationService.handleLike(request,token);
    }

    @Override
    public ResponseEntity<?> addRating(Rating request, String token) {
        return recommendationService.addRating(request,token);
    }

}
