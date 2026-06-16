package hello.orderbridge.claim.service;

import hello.orderbridge.claim.domain.Cancel;
import hello.orderbridge.claim.domain.Claim;
import hello.orderbridge.claim.domain.Exchange;
import hello.orderbridge.claim.domain.Return;
import hello.orderbridge.claim.dto.CancelRequest;
import hello.orderbridge.claim.dto.ExchangeRequest;
import hello.orderbridge.claim.dto.ReturnRequest;
import hello.orderbridge.claim.repository.ClaimRepository;
import hello.orderbridge.common.exception.ClaimNotFoundException;
import hello.orderbridge.common.exception.DuplicateClaimException;
import hello.orderbridge.common.exception.OrderItemNotFoundException;
import hello.orderbridge.enums.claim.RefundMethod;
import hello.orderbridge.enums.order.ItemStatus;
import hello.orderbridge.order.domain.OrderItem;
import hello.orderbridge.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * 클레임 리스트 조회
     * @return
     */
    public List<Claim> getClaimList() {
        return claimRepository.findAll();
    }

    /**
     * 클레임 상세 조회
     * @param id
     * @return
     */
    public Claim getClaim(Long id) {
        return claimRepository.findById(id).orElseThrow(ClaimNotFoundException::new);
    }

    /**
     * 취소 생성
     * @param request
     */
    @Transactional
    public void createCancel(CancelRequest request) {
        Long orderItemId = request.orderItemId();
        OrderItem orderItem = getOrderItem(orderItemId);

        Cancel cancel = Cancel.of(
                orderItem,
                request.reason(),
                request.refundAmount(),
                RefundMethod.valueOf(request.refundMethod())
        );

        claimRepository.save(cancel);

        orderItem.changeStatus(cancel.getRequestedItemStatus());
    }

    /**
     * 반품 조회
     * @param request
     */
    @Transactional
    public void createReturn(ReturnRequest request) {
        Long orderItemId = request.orderItemId();
        OrderItem orderItem = getOrderItem(orderItemId);

        Return _return = Return.of(
                orderItem,
                request.reason(),
                request.pickupAddress(),
                request.carrierCode(),
                request.refundAmount(),
                RefundMethod.valueOf(request.refundMethod())
        );

        claimRepository.save(_return);

        orderItem.changeStatus(_return.getRequestedItemStatus());

    }

    /**
     * 교환 조회
     * @param request
     */
    @Transactional
    public void createExchange(ExchangeRequest request) {
        Long orderItemId = request.orderItemId();
        OrderItem orderItem = getOrderItem(orderItemId);

        Exchange exchange = Exchange.of(
                orderItem,
                request.reason(),
                request.exchangeProductCode(),
                request.deliveryAddress(),
                request.carrierCode()
        );

        claimRepository.save(exchange);

        orderItem.changeStatus(exchange.getRequestedItemStatus());
    }

    /**
     * 클레임 승인
     * @param id
     */
    @Transactional
    public void approveClaim(Long id) {
        Claim claim = getClaim(id);

        claim.approve();
    }

    /**
     * 클레임 반려
     * @param id
     */
    @Transactional
    public void rejectClaim(Long id) {
        Claim claim = getClaim(id);

        claim.reject();
        claim.getOrderItem().changeStatus(ItemStatus.NORMAL);
    }

    /**
     * 클레임 처리 완료
     * @param id
     */
    @Transactional
    public void completeClaim(Long id) {
        Claim claim = getClaim(id);

        claim.complete();
        claim.getOrderItem().changeStatus(claim.getApprovedItemStatus());
    }

    /**
     * 주문 아이템 조회
     * @param orderItemId
     * @return
     */
    private OrderItem getOrderItem(Long orderItemId) {

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(OrderItemNotFoundException::new);

        if (orderItem.getItemStatus() != ItemStatus.NORMAL) {
            throw new DuplicateClaimException();
        }

        return orderItem;
    }
}
