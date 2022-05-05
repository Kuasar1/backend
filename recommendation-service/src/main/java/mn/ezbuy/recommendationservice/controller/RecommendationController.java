package mn.ezbuy.recommendationservice.controller;

import mn.ezbuy.recommendationservice.entity.Like;
import mn.ezbuy.recommendationservice.entity.Rating;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/recommendations")
public interface RecommendationController {

    @PostMapping("/like")
    ResponseEntity<?> handleLike(@RequestBody Like request, @RequestHeader(value = "Authorization") String token);

    @PostMapping("/rating")
    ResponseEntity<?> addRating(@RequestBody Rating request, @RequestHeader(value = "Authorization") String token);

    @GetMapping("/{recommendation}")
    ResponseEntity<?> getForRecommendation(@PathVariable(value = "recommendation") String recommendationType, @RequestParam(value = "userId") Long userId, @RequestHeader(value = "Authorization") String token);

}
