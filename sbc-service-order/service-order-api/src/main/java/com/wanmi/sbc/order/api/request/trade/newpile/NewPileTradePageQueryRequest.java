package com.wanmi.sbc.order.api.request.trade.newpile;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.ChannelType;
import com.wanmi.sbc.common.enums.OrderType;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.NewPileFlowState;
import com.wanmi.sbc.order.bean.enums.PayState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class NewPileTradePageQueryRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 3106550472717776197L;

    /**
     * 订单流程状态
     */
    @ApiModelProperty(value = "订单流程状态")
    private NewPileFlowState flowState;

    /**
     * 订单支付状态
     */
    @ApiModelProperty(value = "订单支付状态")
    private PayState payState;

    /**
     * 订单发货状态
     */
    @ApiModelProperty(value = "订单发货状态")
    private DeliverStatus deliverStatus;

    /**
     * 下单时间上限，精度到天
     */
    @ApiModelProperty(value = "下单时间上限，精度到天")
    private String createdFrom;

    /**
     * 下单时间下限,精度到天
     */
    @ApiModelProperty(value = "下单时间下限,精度到天")
    private String createdTo;

    /**
     * 关键字，用于搜索订单编号或商品名称
     */
    @ApiModelProperty(value = "关键字，用于搜索订单编号或商品名称")
    private String keywords;

    /**
     * 邀请人id
     */
    @ApiModelProperty(value = "邀请人id，用于查询从店铺精选下的单")
    private String inviteeId;

    /**
     * 分销渠道类型
     */
    @ApiModelProperty(value = "分销渠道类型")
    private ChannelType channelType;

    /**
     * 小b端我的客户列表是否是查询全部
     */
    @ApiModelProperty(value = "小b端我的客户列表是否是查询全部")
    private boolean customerOrderListAllType;

    /**
     * 订单类型 0：普通订单；1：积分订单
     */
    @ApiModelProperty(value = "订单类型")
    private OrderType orderType;

    /**
     * 店铺Id
     */
    @ApiModelProperty(value = "店铺编码 精确查询")
    private Long storeId;


    /**
     * 订单列表入口类型：1，全部订单，2，鲸粉页面
     */
    @ApiModelProperty(value = "订单列表入口类型：1，全部订单，2，鲸粉页面")
    private Integer markType;


    /**
     * 订单条件查询，待收货等等
     */
    @ApiModelProperty(value = "订单条件查询，待收货等等")
    private List<FlowState> flowStates;

}
