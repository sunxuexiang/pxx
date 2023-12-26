package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 商品推荐备份
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
public class GoodsRecommendBackupsVO implements Serializable {
    /**
     * 商品编号，采用UUID
     * customer_goods_id
     * customer_id
     * sku_id
     * company_id
     * count
     */
    @ApiModelProperty(value = "商品编号，采用UUID")
    private String customerGoodsId;

    /**
     * 用户
     */
    @ApiModelProperty(value = "用户")
    private String customerId;

    /**
     * sku
     */
    @ApiModelProperty(value = "sku")
    private String skuId;
    /**
     * 公司
     */
    @ApiModelProperty(value = "公司")
    private String companyId;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量D")
    private String count;

    /**
     * 时间
     */
    @ApiModelProperty(value = "时间")
    private Date createTime;
}
