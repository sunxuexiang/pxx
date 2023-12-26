package com.wanmi.sbc.returnorder.bean.dto;

import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyResponseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName TradeDeliveryWayDTO
 * @Description TODO Andriod特约类，由昌启文发起，经戴胜略发展
 * @Author shiy
 * @Date 2023/7/5 8:59
 * @Version 1.0
 */
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeDeliveryWayResMergeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "店铺标识")
    private Long storeId;

    /**
     * 仓库id
     */
    @ApiModelProperty(value = "仓库id")
    private Long wareId;

    /**
     * 运送方式(1:快递配送) {@link DeliverWay}
     */
    @ApiModelProperty(value = "运送方式", notes = "1:托运部 2:快递到家(自费) 6: 上门自提 7: 配送到店(自费) 8: 指定物流 9: 同城配送(自费)")
    private String deliverWay;
    /**
     * 运送方式(1:快递配送) {@link DeliverWay}
     */
    @ApiModelProperty(value = "运送方式", notes = "1:托运部 2:快递到家(自费) 6: 上门自提 7: 配送到店(自费) 8: 指定物流 9: 同城配送(自费)")
    private String deliverWayDesc;

    /**
     * 是否匹配
     */
    @ApiModelProperty(value = "1:匹配 0不匹配")
    private String matchFlag;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @ApiModelProperty(value = "商家类型")
    private Integer companyType;

    /**
     * 免运费起始数量
     */
    @ApiModelProperty(value = "免运费起始数量")
    private String freightFreeNumber;

    /**
     * 模版标识
     */
    @ApiModelProperty(value = "模版标识")
    private String templateId;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;

    @ApiModelProperty(value = "站点自提信息", notes = "站点自提信息")
    private TradeDeliveryWayHomeFlagDTO homeFlagDTO;

    @ApiModelProperty(value = "配送到店提货点信息", notes = "配送到店提货点信息")
    private TradeDeliveryWayHomeFlagDTO toStorePickSit;

    @ApiModelProperty(value = "店铺配送方式信息", notes = "店铺配送方式信息")
    private List<TradeDeliveryWayResDTO> storeWayResDTOList;

    @ApiModelProperty(value = "是否是凑件，1是；0否")
    private Integer patchFlag;

    @ApiModelProperty(value = "批发市场ID")
    private Long mallBulkMarketId;

    @ApiModelProperty(value = "商城ID")
    private Long mallSupplierTabId;

    @ApiModelProperty(value = "配送备注")
    private String deliveryRemark;

    @ApiModelProperty(value = "托运部")
    private LogisticsCompanyResponseVO logisticsCompanyVOTYB;

    @ApiModelProperty(value = "指定的专线")
    private LogisticsCompanyResponseVO logisticsCompanyVOZDZX;
}
