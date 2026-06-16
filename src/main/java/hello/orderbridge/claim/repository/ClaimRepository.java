package hello.orderbridge.claim.repository;

import hello.orderbridge.claim.domain.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long>, ClaimRepositoryCustom {
    List<Claim> findByOrderItemOrderId(Long orderId);
}
