package mn.ezbuy.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mn.ezbuy.orderservice.util.ListToStringConverter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Column(nullable = false, unique = true)
    private String title;

    @NotEmpty
    @Column(nullable = false)
    private String description;

    @NotEmpty
    @Column(nullable = false)
    private String image;

    @Column
    @Convert(converter = ListToStringConverter.class)
    private List<String> categories;

    @Column
    @Convert(converter = ListToStringConverter.class)
    private List<String> color;

    @Column
    private String size;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Boolean isInStock;

    @Column(name = "createdAt", updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updatedAt")
    @CreationTimestamp
    private Date updatedAt;

}
