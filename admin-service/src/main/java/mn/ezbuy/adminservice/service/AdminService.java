package mn.ezbuy.adminservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    public ResponseEntity<?> find(Long id) {
        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
