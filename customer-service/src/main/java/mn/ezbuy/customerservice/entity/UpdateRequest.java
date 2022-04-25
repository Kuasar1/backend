package mn.ezbuy.customerservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest {

    private String username;
    private String firstname;
    private String lastname;
    @Email(message = "{errors.invalid_email}")
    private String email;
    private String password;
    private String image;

}
