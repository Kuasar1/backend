package mn.ezbuy.orderservice.repository;

import mn.ezbuy.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findOrderByUserId(Long userId);

}
