package com.wanmi.sbc.order.api.request.trade;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.order.bean.dto.StoreCommitInfoDTO;
import com.wanmi.sbc.order.bean.enums.OrderSource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>客户端提交囤货订单参数结构，包含除商品信息外的其他必要参数</p>
 * Created by of628-wenzhi on 2017-07-18-下午3:40.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class PileTradeCommitRequest extends BaseRequest {

    private static final long serialVersionUID = -1555919128448507297L;

    @Valid
    @NotEmpty
    @NotNull
    @ApiModelProperty(value = "订单信息")
    private List<StoreCommitInfoDTO> storeCommitInfoList;

    /**
     * 选择的平台优惠券(通用券)id
     */
    @ApiModelProperty(value = "选择的平台优惠券(通用券)id")
    private String commonCodeId;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private Operator operator;

    /**
     * 下单用户
     */
    @ApiModelProperty(value = "下单用户")
    private CustomerVO customer;

    /**
     * 下单用户是否分销员
     */
    private DefaultFlag isDistributor = DefaultFlag.NO;

    @Override
    public void checkParam() {
        storeCommitInfoList.forEach(StoreCommitInfoDTO::checkParam);
    }

    /**
     * 订单来源--区分h5,pc,app,小程序,代客下单
     */
    @ApiModelProperty(value = "订单来源")
    private OrderSource orderSource;

    /**
     * 分销渠道
     */
    @ApiModelProperty(value = "分销渠道")
    private DistributeChannel distributeChannel;

    /**
     * 小店名称
     */
    @ApiModelProperty(value = "小店名称")
    private String shopName;

    /**
     * 平台分销设置开关
     */
    private DefaultFlag openFlag = DefaultFlag.NO;

    /**
     * 使用积分
     */
    @ApiModelProperty(value = "使用积分")
    private Long points;

    /**
     * 分享人id
     */
    @ApiModelProperty(value = "分享人id")
    private String shareUserId;

    /**
     * 是否是秒杀抢购商品订单
     */
    private Boolean isFlashSaleGoods;

    /**
     * 仓库Id
     */
    private Long wareId;

    /**
     * 仓库编码
     */
    private String wareHouseCode;

}
