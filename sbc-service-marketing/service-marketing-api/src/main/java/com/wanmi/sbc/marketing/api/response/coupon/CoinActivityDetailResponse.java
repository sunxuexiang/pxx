package com.wanmi.sbc.marketing.api.response.coupon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoResponseVO;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityStoreDTO;
import com.wanmi.sbc.marketing.bean.enums.CoinActivityFullType;
import com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel;
import com.wanmi.sbc.marketing.bean.vo.CoinActivityGoodsVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-11-23
 * @author Administrator
 */
@ApiModel
@Data
public class CoinActivityDetailResponse implements Serializable {

    private static final long serialVersionUID = -2632254645824662407L;

    @ApiModelProperty(value = "优惠券活动id")
    private String activityId;

    /**
     * 优惠券活动名称
     */
    @ApiModelProperty(value = "优惠券活动名称")
    private String activityName;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     * 是否暂停 ，1 暂停
     */
    private BoolFlag pauseFlag;

    /**
     * 商户id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * 仓库id
     */
    @ApiModelProperty(value = "仓库id")
    private Long wareId;

    /**
     * 关联的客户等级   -2：指定客户 -1:全部客户 0:全部等级 other:其他等级
     * {@link MarketingJoinLevel}
     */
    @ApiModelProperty(value = "关联的客户等级", dataType = "com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel")
    private String joinLevel;

    @ApiModelProperty(value = "操作人")
    private String createPerson;

    /**
     * 购买指定商品赠券商品信息id列表
     */
    @ApiModelProperty(value = "购买指定商品赠券商品信息id列表 单个活动最多配置100个商品")
    private List<CoinActivityGoodsVo> coinActivityGoodsVoList;

    /**
     * 购买指定商品赠券条件，0购所有赠，1购任一赠
     */
    @ApiModelProperty(value = "购买指定商品赠券条件，0购所有赠，1购任一赠")
    private CoinActivityFullType coinActivityFullType;

    /**
     * 是否叠加（0：否，1：是）
     */
    @ApiModelProperty(value = "是否叠加（0：否，1：是）")
    private DefaultFlag isOverlap = DefaultFlag.NO;

    /**
     * 是否叠加（0：否，1：是）
     */
    @ApiModelProperty(value = "鲸币数量")
    private BigDecimal coinNum;

    @ApiModelProperty(value = "商品信息")
    private GoodsInfoResponseVO goodsList;

    private BoolFlag terminationFlag;

    /**
     * 真正的终止时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime realEndTime;

    @ApiModelProperty(value = "是否显示后端状态")
    private Boolean isShowActiveStatus = Boolean.FALSE;
    
    @ApiModelProperty(value = "订单赠金币配置的商家")
    private List<CoinActivityStoreDTO> coinActivityStore;


}
