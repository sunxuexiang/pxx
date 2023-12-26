package com.wanmi.sbc.order.bean.vo;

import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.order.bean.enums.OrderSource;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.enums.ReturnType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class ReturnOrderNewVO implements Serializable {

    /**
     * 商家信息
     */
    @ApiModelProperty(value = "商家信息")
    private CompanyVO company;

    /**
     * 退单号
     */
    @ApiModelProperty(value = "退单号")
    private String id;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    @NotBlank
    private String tid;

    @ApiModelProperty(value = "子订单编号")
    private String ptid;

    /**
     * 退单类型
     */
    @ApiModelProperty(value = "退单类型")
    private ReturnType returnType;

    /**
     * 退货单状态
     */
    @ApiModelProperty(value = "退货单状态")
    private ReturnFlowState returnFlowState;

    /**
     * 退款单状态
     */
    @ApiModelProperty(value = "退款单状态")
    private RefundStatus refundStatus;

    /**
     * 订单来源--区分h5,pc,app,小程序,代客下单
     */
    @ApiModelProperty(value = "订单来源")
    private OrderSource orderSource;

    /**
     * 退货总金额
     */
    @ApiModelProperty(value = "退货总金额")
    private ReturnPriceVO returnPrice;

    /**
     * 退货商品照片
     */
    @ApiModelProperty(value = "退货商品照片")
    private List<String> returnItemPics;

    /**
     * 退货商品总数
     */
    @ApiModelProperty(value = "退货商品总数")
    private Integer returnOrderNum;

    /**
     * 是否可退标识
     */
    @ApiModelProperty(value = "是否可退标识", dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private Boolean canReturnFlag;

    /**
     * 是否平台自营
     */
    @ApiModelProperty(value = "是否平台自营",dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private Boolean isSelf;

}
