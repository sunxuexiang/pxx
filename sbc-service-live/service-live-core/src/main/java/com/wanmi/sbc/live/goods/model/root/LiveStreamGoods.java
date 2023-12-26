package com.wanmi.sbc.live.goods.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class

LiveStreamGoods extends BaseEntity {
    private static final long serialVersionUID = 1791727470875246381L;

    private Integer id;
    /**
     * 直播id
     */
    private Integer liveId;
    /**
     * 商品SPU_ID
     */
    private String goodsId;
    /**
     * 直播商品SKU_ID
     */
    private String goodsInfoId;

    /**
     * 仓库id
     */
    private Long wareId;

    /**
     * 0 批发 1散批
     */
    private Long goodsType;

    /**
     * 商品直播间上下架  0 下架 1上架
     */
    private Long goodsStatus;

    /**
     * 讲解标识,0:未讲解1:讲解中
     */
    private Integer explainFlag;

    /**
     * 删除标识,0:未删除1:已删除
     */
    private Integer delFlag;

    /**
     * 父级GoodsInfoId
     */
    private String parentGoodsInfoId;

    /**
     * 店铺ID
     */
    private Long storeId;
}
