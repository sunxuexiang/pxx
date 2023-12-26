package com.wanmi.sbc.wms.api.request.wms;

import com.alibaba.fastjson.annotation.JSONField;
import com.wanmi.sbc.wms.api.request.WmsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @ClassName: SalesOrderDetails
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/8 14:40
 * @Version: 1.0
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WMSPushOrderDetailsRequest extends WmsBaseRequest {
    private static final long serialVersionUID = -884962266701057125L;
    @ApiModelProperty(value = "产品")
    @NotBlank
    private String sku;

    @ApiModelProperty(value = "订货数")
    @NotBlank
    private BigDecimal qtyOrdered;

    @ApiModelProperty(value = "订单数")
    @JSONField(name = "qtyOrdered_each")
    private BigDecimal qtyOrdered_each;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "批次属性01")
    private String lotAtt01;

    @ApiModelProperty(value = "特价商品的标识")
    private Integer userDefine1;

    @ApiModelProperty(value = "商品的编码")
    private String userDefine5;

    @ApiModelProperty(value = "ERP的仓库地址")
    private String lotatt04;

    @ApiModelProperty(value = "散批商品包装规格")
    private String lotAtt03;


    /**
     * 根据sku的数依次递增
     */
    @ApiModelProperty(value = "行号")
    private Integer lineNo;

    @ApiModelProperty(name = "会员的Id")
    private String customerId;
    
    @ApiModelProperty(name = "囤货订单号")
    private String dedi07;

    @ApiModelProperty(value = "EDI信息(是否赠品；Y,N)")
    private String dedi04;

    @ApiModelProperty(name = "商品拆箱规格标志")
    private String dedi08;





}
