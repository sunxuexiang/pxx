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
 * @author lm
 * @date 2022/10/10 9:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class NewPileTradeListExportRequest implements Serializable {

    /**
     * 分页参数
     */
    @ApiModelProperty(value = "分页参数")
    private NewPileTradeQueryDTO newPileTradeQueryDTO;

}
