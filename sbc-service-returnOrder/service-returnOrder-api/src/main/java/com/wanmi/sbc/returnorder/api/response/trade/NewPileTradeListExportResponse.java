package com.wanmi.sbc.returnorder.api.response.trade;

import com.wanmi.sbc.returnorder.bean.vo.NewPileTradeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: lm
 * @Description:
 * @Date 2022-10-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class NewPileTradeListExportResponse implements Serializable {

    /**
     * 交易单列表
     */
    @ApiModelProperty(value = "交易单列表")
    private List<NewPileTradeVO> newPileTradeVOList;

}
