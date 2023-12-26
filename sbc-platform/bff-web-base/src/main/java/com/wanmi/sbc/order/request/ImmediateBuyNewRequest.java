package com.wanmi.sbc.order.request;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: gaomuwei
 * @Date: Created In 下午3:28 2019/3/6
 * @Description:
 */
@ApiModel
@Data
public class ImmediateBuyNewRequest {

    @Valid
    @NotEmpty
    private List<ImmediateBuyRequest> tradeItemRequests;

    @ApiModelProperty("是否匹配到仓")
    private Boolean matchWareHouseFlag;

    @ApiModelProperty("仓库id")
    private  Long wareId;

    /**
     * 是否开店礼包
     */
    @ApiModelProperty("是否开店礼包")
    private DefaultFlag storeBagsFlag;

    @ApiModelProperty("选择的赠品skuIds")
    private List<String> choseGiftIds;

    @ApiModelProperty("是否预售订单")
    private boolean presellFlag;
}
