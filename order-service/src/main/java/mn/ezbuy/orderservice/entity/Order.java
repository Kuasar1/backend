package mn.ezbuy.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToMany(cascade = CascadeType.MERGE)
    @LazyCollection(value = LazyCollectionOption.FALSE)
    @JoinTable(name = "orders_products",
            joinColumns = {@JoinColumn(name = "ORDER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID")})
    private List<Product> products;

    @Column(nullable = false)
    private Double amount;

    @ManyToOne(cascade = CascadeType.ALL)
    @LazyCollection(value = LazyCollectionOption.FALSE)
    @JoinTable(name = "orders_addresses",
            joinColumns = {@JoinColumn(name = "ORDER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ADDRESS_ID", referencedColumnName = "ID")})
    private Address addresses;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(nullable = false)
    private Long quantity;

}
