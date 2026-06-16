package hello.orderbridge.claim.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.orderbridge.claim.domain.Claim;
import hello.orderbridge.claim.dto.ClaimSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static hello.orderbridge.claim.domain.QClaim.claim;

@RequiredArgsConstructor
public class ClaimRepositoryImpl implements ClaimRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Claim> search(ClaimSearchCondition condition, Pageable pageable) {
        List<Claim> content = queryFactory
                .selectFrom(claim)
                .where(buildCondition(condition))
                .orderBy(claim.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(claim.count())
                .from(claim)
                .where(buildCondition(condition));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanBuilder buildCondition(ClaimSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();

        if (condition.getStatus() != null) {
            builder.and(claim.status.eq(condition.getStatus()));
        }

        return builder;
    }
}
