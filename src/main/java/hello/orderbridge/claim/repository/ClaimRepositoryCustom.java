package hello.orderbridge.claim.repository;

import hello.orderbridge.claim.domain.Claim;
import hello.orderbridge.claim.dto.ClaimSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClaimRepositoryCustom {
    Page<Claim> search(ClaimSearchCondition condition, Pageable pageable);
}
