package com.wanmi.sbc.goods.bean.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * 预售库存更新记录表
 */
@ApiModel
@Data
public class GoodsInfoPresellRecordDTO implements Serializable {

    /**
     * 编号
     */
    private Long id;

    /**
     * SPU标识
     */
    private String goodsId;
    /**
     * SKU标识
     */
    private String goodsInfoId;

    /**
     *店铺id
     */
    private Long storeId;


    /**
     *仓库id
     */
    private Long wareId;

    /**
     *当前预售库存
     */
    private Long presellCount;

    /**
     * 库存变更类型:0扣减库存，1增加库存
     */
    private Integer type;


    /**
     *订单id
     */
    private String tradeId;
}
