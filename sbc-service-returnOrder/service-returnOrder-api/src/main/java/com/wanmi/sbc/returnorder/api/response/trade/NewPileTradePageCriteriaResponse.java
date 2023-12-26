package com.wanmi.sbc.returnorder.api.response.trade;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.returnorder.bean.vo.NewPileTradeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class NewPileTradePageCriteriaResponse implements Serializable {

    /**
     * 分页数据
     */
    @ApiModelProperty(value = "分页数据")
    private MicroServicePage<NewPileTradeVO> tradePage;

}
