package hello.orderbridge.order.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.orderbridge.order.domain.Order;
import hello.orderbridge.order.dto.OrderSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static hello.orderbridge.order.domain.QOrder.*;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> search(OrderSearchCondition condition, Pageable pageable) {

        List<Order> content = queryFactory.selectFrom(order)
                .where(buildCondition(condition))
                .orderBy(order.orderedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .where(buildCondition(condition));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne); // (지연 실행 — LongSupplier로 전달)
    }

    private BooleanBuilder buildCondition(OrderSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();

        if (condition.getChannelType() != null) {
            builder.and(order.channel.type.eq(condition.getChannelType()));
        }

        if (condition.getStatus() != null) {
            builder.and(order.status.eq(condition.getStatus()));
        }

        if (condition.getStartDate() != null) {
            builder.and(order.orderedAt.goe(condition.getStartDate().atStartOfDay()));
        }

        if (condition.getEndDate() != null) {
            builder.and(order.orderedAt.loe(condition.getEndDate().plusDays(1).atStartOfDay()));
        }

        return builder;
    }
}
