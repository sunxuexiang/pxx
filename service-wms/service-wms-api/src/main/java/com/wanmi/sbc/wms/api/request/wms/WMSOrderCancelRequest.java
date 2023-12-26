package com.wanmi.sbc.wms.api.request.wms;

import com.alibaba.fastjson.annotation.JSONField;
import com.wanmi.sbc.wms.api.request.WmsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: OrderCancel
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 19:51
 * @Version: 1.0
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WMSOrderCancelRequest extends WmsBaseRequest {


    private static final long serialVersionUID = -7066481983613576884L;
    @ApiModelProperty(value = "上游来源订单号")
    @NotBlank
    private String docNo;

    @ApiModelProperty(value = "作业单类型")
    @NotBlank
    private String orderType;

    @ApiModelProperty(value = "货主ID")
    @NotBlank
    private String customerId;

    @ApiModelProperty(value = "仓库ID")
    @NotBlank
    private String warehouseId;

    @ApiModelProperty(value = "取消原因")
    private String erpCancelReason;

    @ApiModelProperty(value = "收货人Id")
    private String consigneeId;

    /**
     * 订单来源 001代表电商1 002代表电扇2
     */
    @JSONField(name = "h_EDI_09")
    private String storeSource;

}
