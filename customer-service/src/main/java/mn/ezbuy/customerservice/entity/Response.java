package mn.ezbuy.customerservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    private Long id;
    private String username;
    private String email;
    private boolean isAdmin;
    private String accessToken;

}
