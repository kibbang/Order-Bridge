package hello.orderbridge.collector.dto.external.ebay.auction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuctionOrder {
    // 채널 주문번호 (중복 체크 키)
    @JsonProperty("OrderNo")
    private Long orderNo;

    // 주문 일시
    @JsonProperty("OrderDate")
    private LocalDateTime orderDate;

    // 주문자 이름
    @JsonProperty("BuyerName")
    private String buyerName;

    // 주문자 연락처
    @JsonProperty("BuyerMobileTel")
    private String buyerMobileTel;

    // 수령인 이름
    @JsonProperty("ReceiverName")
    private String receiverName;

    // 수령인 연락처
    @JsonProperty("HpNo")
    private String hpNo;

    // 배송지 주소
    @JsonProperty("DelFullAddress")
    private String delFullAddress;

    // 배송 메모
    @JsonProperty("DelMemo")
    private String delMemo;

    // 주문 총액 (ex. "6800.0000")
    @JsonProperty("OrderAmount")
    private String orderAmount;

    // 상품 코드
    @JsonProperty("SiteGoodsNo")
    private String siteGoodsNo;

    // 상품명
    @JsonProperty("GoodsName")
    private String goodsName;

    // 수량
    @JsonProperty("ContrAmount")
    private Integer contrAmount;

    // 단가 (ex. "3400.0000")
    @JsonProperty("SalePrice")
    private String salePrice;
}
