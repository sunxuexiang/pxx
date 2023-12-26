package com.wanmi.sbc.order.request;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>订单商品快照验证请求结构</p>
 * Created by of628-wenzhi on 2017-07-13-上午9:15.
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class TradeItemConfirmRequest extends BaseRequest {

    private static final long serialVersionUID = -3106790833666168436L;

    /**
     * 商品信息，必传
     */
    @ApiModelProperty(value = "商品信息")
//    @NotEmpty
    @Valid
    private List<TradeItemRequest> tradeItems;

    /**
     * 商品信息，必传
     */
    @ApiModelProperty(value = "入驻商品信息")
    @Valid
    private List<TradeItemRequest> thirdSellerTradeItems;

    /**
     * 订单营销信息快照
     */
    @ApiModelProperty(value = "订单营销信息快照")
    private List<TradeMarketingDTO> tradeMarketingList;

    /**
     * 商品信息，必传
     */
    @ApiModelProperty(value = "零售商品信息")
    @Valid
    private List<TradeItemRequest> retailTradeItems;

    /**
     * 订单营销信息快照
     */
    @ApiModelProperty(value = "零售订单营销信息快照")
    private List<TradeMarketingDTO> retailTradeMarketingList;




    /**
     * 散批商品信息
     */
    @ApiModelProperty(value = "散批商品信息")
//    @Valid
    private List<TradeItemRequest> bulkTradeItems;





    /**
     * 是否强制确认，用于营销活动有效性校验，true: 无效依然提交， false: 无效做异常返回
     */
    @ApiModelProperty(value = "是否强制确认，用于营销活动有效性校验,true: 无效依然提交， false: 无效做异常返回")
    public boolean forceConfirm;

    /**
     * 仓库Id
     */
    @ApiModelProperty(value = "仓库Id")
    private Long wareId;

    private String userId;

    /**
     * 是否为关键字查询
     */
    @ApiModelProperty(value = "是否能匹配仓")
    private Boolean matchWareHouseFlag;

    /**
     * 是否预售商品
     */
    private boolean presellFlag;

}
