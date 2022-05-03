package mn.ezbuy.adminservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/recommendations")
public interface RecommendationController {

    @GetMapping("/{recommendation}")
    ResponseEntity<?> getForRecommendation(@PathVariable(value = "recommendation") String recommendationType, @RequestParam(value = "userId") Long userId, @RequestHeader(value = "Authorization") String token);

}
