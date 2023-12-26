package com.wanmi.sbc.returnorder.api.request.trade;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.NetWorkVO;
import com.wanmi.sbc.returnorder.bean.dto.StoreCommitInfoDTO;
import com.wanmi.sbc.returnorder.bean.enums.OrderSource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>客户端提交订单参数结构，包含除商品信息外的其他必要参数</p>
 * Created by of628-wenzhi on 2017-07-18-下午3:40.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class TradeCommitRequest extends BaseRequest {

    private static final long serialVersionUID = -1555919128448507297L;

    /**
     * 订单收货地址id，必传
     */
    @ApiModelProperty(value = "订单收货地址id")
    @NotBlank
    private String consigneeId;

    /**
     * 收货地址详细信息(包含省市区)，必传
     */
    @ApiModelProperty(value = "收货地址详细信息(包含省市区)")
    @NotBlank
    private String consigneeAddress;

    /**
     * 收货地址修改时间，可空
     */
    @ApiModelProperty(value = "收货地址修改时间")
    private String consigneeUpdateTime;

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
     * 是否强制提交，用于营销活动有效性校验，true: 无效依然提交， false: 无效做异常返回
     */
    @ApiModelProperty(value = "是否强制提交",dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    public boolean forceCommit;

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


    @ApiModelProperty(value = "用户收获地址省")
    Long provinceId;
    @ApiModelProperty(value = "用户收获地址市")
    Long cityId;

    /**
     * 是否是秒杀抢购商品订单
     */
    private Boolean isFlashSaleGoods;

    /**
     * 仓库Id
     */
    private Long wareId;

    /**
     * 仓库Id
     */
    private Long bulkWareId;

    /**
     * 仓库名称
     */
    private String wareName;

    /**
     * 仓库编码
     */
    private String wareHouseCode;

    //
    private Long cityCode;

    /**
     * 使用余额
     */
    @ApiModelProperty(value = "使用余额")
    private BigDecimal walletBalance;

    /**
     * 使用战点自提
     */
    private NetWorkVO netWorkVO;

}
