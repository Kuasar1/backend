package mn.ezbuy.customerservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Column(nullable = false, unique = true)
    private String username;

    @NotEmpty
    @Column(nullable = false)
    private String firstname;

    @NotEmpty
    @Column(nullable = false)
    private String lastname;

    @NotEmpty
    @Column(nullable = false, unique = true)
    @Email(message = "{errors.invalid_email}")
    private String email;

    @NotEmpty
    @Column(nullable = false)
    private String password;

    private String image;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "customers_roles",
            joinColumns = {@JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})
    private List<Role> roles;

}
