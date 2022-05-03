package mn.ezbuy.adminservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mn.ezbuy.adminservice.entity.Rating;
import mn.ezbuy.adminservice.repository.RatingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WriterService {

    @Autowired
    private final RatingRepository ratingRepository;

    @SneakyThrows
    public ResponseEntity<?> write() {
        try {
            File csv = new File(System.getProperty("user.dir") + "/admin-service/src/main/data/ratings.csv");
            PrintWriter out = new PrintWriter(csv);
            List<Rating> ratings = ratingRepository.findAll();
            for(Rating rating : ratings) {
                out.printf("%d,%d,%.1f\n",rating.getUserId(),rating.getProductId(), (double) rating.getRating());
            }
            out.close();
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

}
