package com.wanmi.sbc.wms.requestwms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName: OrderCancel
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 19:51
 * @Version: 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WMSOrderCancel implements Serializable {
    private static final long serialVersionUID = 1307419817755972702L;

    @ApiModelProperty(value = "作业单号")
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




}
