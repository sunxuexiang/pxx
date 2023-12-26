package com.wanmi.sbc.order.bean.dto;

import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: SupplierAppOrderList
 * @Description: TODO
 * @Date: 2020/7/25 10:09
 * @Version: 1.0
 */
@Data
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierAppOrderListDTO implements Serializable {

    private static final long serialVersionUID = -7393893974232928122L;

    @ApiModelProperty(value = "发货清单")
    @NotEmpty
    @Valid
    private List<TradeItemDTO> tradeItems;


    @NotNull
    @ApiModelProperty(value = "配送方式")
    private DeliverWay deliverWay;


    @ApiModelProperty(value = "卖家备注")
    private String sellerRemark;

    @ApiModelProperty(value = "订单附件")
    private String encloses;

    /**
     * 仓库Id
     */
    @ApiModelProperty(value = "仓库Id")
    @NotNull
    private Long wareId;

    @ApiModelProperty(value = "仓库编号")
    private String wareHouseCode;
}
