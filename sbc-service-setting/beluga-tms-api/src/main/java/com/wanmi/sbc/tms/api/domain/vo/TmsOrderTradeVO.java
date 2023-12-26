package com.wanmi.sbc.tms.api.domain.vo;



import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 承运单关联订单表
 * </p>
 *
 * @author xyy
 * @since 2023-09-18
 */
@Getter
@Setter
public class TmsOrderTradeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //   ("运单id")
    private String tmsOrderId;

    //   ("订单id")
    private String tradeOrderId;

    //   ("p订id")
    private String tradeParentOrderId;

    //   ("店铺id")
    private Long storeId;

    //   ("店铺名称")
    private String storeName;

    //   ("订单金额")
    private BigDecimal tradeAmount;

    //   ("商品总件数")
    private Integer goodsTotalNum;

    //   ("支付订单号")
    private String payOrderNo;

    //   ("订单类型")
    private Integer tradeType;

    //   ("订单状态")
    private String tradeStatus;

    //   ("订单创建时间")
    private LocalDateTime tradeTime;

    //   ("备注")
    private String remark;

    //   ("删除标志（0代表存在 1代表删除）")
    private Integer delFlag;


}
