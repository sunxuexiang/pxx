package com.wanmi.sbc.marketing.suittobuy.model.root;

import com.wanmi.sbc.marketing.common.BaseBean;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "marketing_suit_detail")
public class MarketingSuitDetail extends BaseBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suit_id")
    private Long suitId;

    /**
     * 套装id
     */
    @Column(name = "marketing_id")
    private Long marketingId;

    /**
     * 促销商品id
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 套装内商品的营销活动id
     */
    @Column(name = "goods_marketing_id")
    private Long goodsMarketingId;

    /**
     * 套装内商品买赠（赠品id）
     */
    @Column(name = "gift_id")
    private String giftId;
}
