package com.wanmi.sbc.wms.api.request.wms;

import com.wanmi.sbc.wms.api.request.WmsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @ClassName: OrderDetails
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/7 18:36
 * @Version: 1.0
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WMSChargeBackDetailsRequest extends WmsBaseRequest {
    private static final long serialVersionUID = -5738668304178667922L;

    @ApiModelProperty(value = "退货的数量")
    @NotBlank
    private BigDecimal expectedQty;

    @ApiModelProperty(value = "产品")
    @NotBlank
    private String sku;

    @ApiModelProperty(value = "拆箱号")
    private String dedi06;

    @ApiModelProperty(value = "总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "行号")
    private Integer lineNo;

    @ApiModelProperty(value = "批次属性01")
    private String lotAtt01;

    @ApiModelProperty(value = "订单号")
    private String referenceNo;


    @ApiModelProperty(value = "商品的编码")
    private String userDefine5;

}
