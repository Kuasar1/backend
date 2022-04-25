package mn.ezbuy.adminservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
public interface AdminController {

    @GetMapping("/find/{id}")
    ResponseEntity<?> find(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);

}
