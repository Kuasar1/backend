package mn.ezbuy.productservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rankings")
public class Ranking implements Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Long productId;

    @Column
    private Long frequency;

    @Column
    private Long total;

    @Column
    private Long average;

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
