package com.wanmi.sbc.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.util.CustomLocalDateDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.marketing.bean.vo.TradeCouponVO;
import com.wanmi.sbc.marketing.bean.vo.TradeGrouponVO;
import com.wanmi.sbc.marketing.bean.vo.TradeMarketingVO;
import com.wanmi.sbc.order.bean.enums.OrderSource;
import com.wanmi.sbc.order.bean.enums.PaymentOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 订单状态
 * Created by jinwei on 14/3/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class TradeExport implements Serializable {

    private static final long serialVersionUID = -6414564526969451201L;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String id;

    @ApiModelProperty(value = "开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 购买人姓名
     */
    @ApiModelProperty(value = "客户名称")
    private String buyerName;

    /**
     * 购买人姓名
     */
    @ApiModelProperty(value = "客户账号")
    private String buyerAccount;

    /**
     * 等级名称
     */
    @ApiModelProperty(value = "客户级别")
    private String levelName;

    /**
     * 收货人名称
     */
    @ApiModelProperty(value = "收货人")
    private String consigneeName;

    /**
     * 收货人电话
     */
    @ApiModelProperty(value = "收货手机")
    private String consigneePhone;

    /***
     * 详细地址(包含省市区）
     */
    @ApiModelProperty(value = "收货人地址")
    private String detailAddress;

    /**
     * 描述
     */
    @ApiModelProperty(value = "支付方式")
    private String desc;

    @ApiModelProperty(value = "配送方式")
    private String deliverWay;

    public void setDeliverWay(DeliverWay deliverWay) {
        switch (deliverWay) {
            case PICK_SELF:
                this.deliverWay = "自提";
                break;
            case LOGISTICS:
                this.deliverWay ="物流";
                break;
            case EXPRESS:
                this.deliverWay = "快递到家";
                break;
            case DELIVERY_HOME:
                this. deliverWay = "本地配送";
                break;
            default:
                this.deliverWay = "其他";
                break;
        }
    }
}
