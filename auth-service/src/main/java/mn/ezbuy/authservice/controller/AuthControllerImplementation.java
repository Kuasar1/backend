//package mn.ezbuy.authservice.controller;
//
//import lombok.RequiredArgsConstructor;
//import mn.ezbuy.authservice.service.AuthService;
//import mn.ezbuy.customerservice.entity.LoginRequest;
//import mn.ezbuy.customerservice.entity.RegisterRequest;
//import mn.ezbuy.customerservice.entity.UpdateRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@Component
//@RequestMapping("/auth")
//public class AuthControllerImplementation implements AuthController{
//
//    @Autowired
//    private final AuthService authService;
////
////    @Override
////    public ResponseEntity<?> register(RegisterRequest request) {
////        return authService.register(request);
////    }
////
////    @Override
////    public ResponseEntity<?> login(LoginRequest request) {
////        return authService.login(request);
////    }
//
//    @Override
//    public ResponseEntity<?> update(UpdateRequest request, String token, Long id) {
//        return authService.update(request, token, id);
//    }
//
//    @Override
//    public ResponseEntity<?> delete(String token, Long id) {
//        return authService.delete(token, id);
//    }
//
//}
