package com.wanmi.sbc.order.bean.dto;

import com.wanmi.sbc.customer.bean.dto.CustomerLevelDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.order.bean.enums.FollowFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-11-30
 */
@Data
@Builder
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseSaveDTO implements Serializable {

    private static final long serialVersionUID = -2197802654540670472L;

    /**
     * 编号
     */
    @ApiModelProperty(value = "编号")
    private List<Long> followIds;

    /**
     * SKU编号
     */
    @ApiModelProperty(value = "SKU编号")
    private String goodsInfoId;

    /**
     * 批量SKU编号
     */
    @ApiModelProperty(value = "批量SKU编号")
    private List<String> goodsInfoIds;


    /**
     * 除数标识
     */
    @ApiModelProperty(value = "除数标识")
    private BigDecimal divisorFlag = BigDecimal.ONE;

    /**
     * 批量sku
     */
    @ApiModelProperty(value = "批量sku")
    private List<GoodsInfoDTO> goodsInfos;

    /**
     * 会员编号
     */
    @ApiModelProperty(value = "会员编号")
    private String customerId;

    /**
     * 购买数量
     */
    @ApiModelProperty(value = "购买数量")
    @Range(min = 1)
    private Long goodsNum;

    /**
     * 收藏标识
     */
    @Enumerated
    @ApiModelProperty(value = "收藏标识")
    private FollowFlag followFlag;

    /**
     * 校验库存
     */
    @ApiModelProperty(value = "是否校验库存",dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    @Builder.Default
    private Boolean verifyStock = true;

    /**
     * 当前客户等级
     */
    @ApiModelProperty(value = "当前客户等级")
    private CustomerLevelDTO customerLevel;

    /**
     * 是否赠品 true 是 false 否
     */
    @ApiModelProperty(value = "是否赠品",dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    @Builder.Default
    private Boolean isGift = false;

    /**
     * 邀请人id-会员id
     */
    @ApiModelProperty(value = "邀请人id")
    String inviteeId;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库Id")
    private Long wareId;

    /**
     * 旧的仓库ID
     */
    @ApiModelProperty(value = "旧的仓库Id，切换地址时候使用")
    private Long oldWareId;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "散批仓库Id")
    private Long bulkWareId;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "旧的散批仓库Id，切换地址时候使用")
    private Long oldBulkwareId;


    /**
     * 订单编号 ===》 囤货明细
     */
    private String orderCode;

    /**
     * 订单pid
     */
    private String pid;

    /**
     * 订单总金额
     */
    private BigDecimal orderTotalPrice;


    /**
     * 拆箱规格主键
     */
    @ApiModelProperty(value = "拆箱规格主键")
    private Long devanningId;


    /**
     * 批量SKU编号
     */
    @ApiModelProperty(value = "批量拆箱编号")
    private List<Long> devanningIds;


    @ApiModelProperty(value = "市")
    private Long cityId;
    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;


    //subType 默认 0
    //0是非囤货
    //1是囤货

    @ApiModelProperty(value = "区分囤货")
    private Integer subType = 0;


}
