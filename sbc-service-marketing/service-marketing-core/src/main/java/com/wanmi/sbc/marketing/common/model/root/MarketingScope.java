package com.wanmi.sbc.marketing.common.model.root;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.marketing.common.BaseBean;
import lombok.Data;

import javax.persistence.*;

/**
 * 营销和商品关联关系
 */
@Data
@Entity
@Table(name = "marketing_scope")
public class MarketingScope extends BaseBean {

    /**
     * 货品与促销规则表Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "marketing_scope_id")
    private Long marketingScopeId;

    /**
     * 促销Id
     */
    @Column(name = "marketing_id")
    private Long marketingId;

    /**
     * 促销范围Id
     */
    @Column(name = "scope_id")
    private String scopeId;

    /**
     * 是否终止
     */
    @Column(name = "termination_flag")
    @Enumerated
    private BoolFlag terminationFlag=BoolFlag.NO;

    /**
     * 是否为必选商品  0：非必选  1：必选
     */
    @Column(name = "whether_choice")
    private BoolFlag whetherChoice;

    /**
     * 限购数量
     */
    @Column(name = "purchase_num")
    private Long purchaseNum;

    /**
     * 单用户限购数量
     */
    @Column(name = "per_user_purchase_num")
    private Long perUserPurchaseNum;
}
