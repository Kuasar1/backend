package mn.ezbuy.adminservice.controller;

import lombok.RequiredArgsConstructor;
import mn.ezbuy.adminservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Component
@RequestMapping("/admin")
public class AdminControllerImplementation implements AdminController{

    @Autowired
    private final AdminService adminService;

    @Override
    public ResponseEntity<?> find(String token, Long id) {
        return adminService.find(id);
    }
}