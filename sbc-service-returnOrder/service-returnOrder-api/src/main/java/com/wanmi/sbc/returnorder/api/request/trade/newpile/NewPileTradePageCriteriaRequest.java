package com.wanmi.sbc.returnorder.api.request.trade.newpile;

import com.wanmi.sbc.returnorder.bean.dto.NewPileTradeQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-04 11:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class NewPileTradePageCriteriaRequest implements Serializable {

    /**
     * 是否是可退单查询
     */
    @ApiModelProperty(value = "是否是可退单查询",dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private boolean isReturn;

    /**
     * 分页参数
     */
    @ApiModelProperty(value = "分页参数")
    private NewPileTradeQueryDTO tradePageDTO;
}
