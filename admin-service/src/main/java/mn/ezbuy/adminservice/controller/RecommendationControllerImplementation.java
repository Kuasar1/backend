package mn.ezbuy.adminservice.controller;

import lombok.RequiredArgsConstructor;
import mn.ezbuy.adminservice.service.RecommendationService;
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
}
