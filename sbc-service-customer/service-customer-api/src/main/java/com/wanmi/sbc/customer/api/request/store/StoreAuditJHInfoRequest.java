package com.wanmi.sbc.customer.api.request.store;

import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: songhanlin
 * @Date: Created In 下午3:51 2017/11/6
 * @Description: 驳回/通过 审核
 */
@ApiModel
@Data
public class StoreAuditJHInfoRequest implements Serializable {

    private static final long serialVersionUID = 3302655499380865353L;

    /**
     * 店铺Id
     */
    @ApiModelProperty(value = "店铺Id")
    private Long storeId;

    @ApiModelProperty(value = "分账比例")
    private BigDecimal shareRatio;

    @ApiModelProperty(value = "周期")
    private Integer settlementCycle;

    @ApiModelProperty(value = "建行商户号")
    private String constructionBankMerchantNumber;

}
