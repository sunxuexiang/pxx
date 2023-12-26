package com.wanmi.sbc.order.bean.dto;

import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyResponseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName TradeDeliveryWayDTO
 * @Description TODO
 * @Author shiy
 * @Date 2023/7/5 8:59
 * @Version 1.0
 */
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeDeliveryWayResDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "店铺标识")
    private Long storeId;

    private Long companyId;

    /**
     * 仓库id
     */
    @ApiModelProperty(value = "仓库id")
    private Long wareId;

    /**
     * 运送方式(1:快递配送) {@link DeliverWay}
     */
    private DeliverWay deliverWay;

    private String deliverWayDesc;

    /**
     * 是否匹配
     */
    private Integer matchFlag;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    private Integer companyType;

    /**
     * 免运费起始数量
     */
    private Long freightFreeNumber;

    /**
     * 模版标识
     */
    private Long templateId;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;

    @ApiModelProperty(value = "站点自提信息", notes = "站点自提信息")
    private TradeDeliveryWayHomeFlagDTO homeFlagDTO;

    @ApiModelProperty(value = "配送到店提货点信息", notes = "配送到店提货点信息")
    private TradeDeliveryWayHomeFlagDTO toStorePickSit;
    @ApiModelProperty(value = "是否是凑件，1是；0否")
    private Integer patchFlag;

    @ApiModelProperty(value = "批发市场ID")
    private Long mallBulkMarketId;

    @ApiModelProperty(value = "商城ID")
    private Long mallSupplierTabId;

    private String mallBulkMarkeName;

    private String mallSupplierTabName;

    @ApiModelProperty(value = "配送备注")
    private String deliveryRemark;
    @ApiModelProperty(value = "物流信息")
    private LogisticsCompanyResponseVO logisticsCompanyVO;
}
