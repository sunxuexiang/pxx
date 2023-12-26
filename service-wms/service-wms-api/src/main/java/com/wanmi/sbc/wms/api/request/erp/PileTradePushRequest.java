package com.wanmi.sbc.wms.api.request.erp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.util.CustomLocalDateDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 推金蝶囤货销售订单表头
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class PileTradePushRequest implements Serializable {

    private static final long serialVersionUID = 2560536860094739738L;
    /**
     * 囤货单号
     */
   private String fBillNo;

    /**
     * 日期
     */
    private String fDate;

    /**
     * 组织
     */
    private Map fSaleOrgId;

    /**
     * 客户
     */
   private Map fCustId;

    /**
     * 销售部门（不传）
     */
   private String fSaleDeptId;

    /**
     * 销售员
     */
   private Map fSalerId;

    /**
     * 订单状态
     */
   private String fOrderStatus;

    /**
     * 联系电话
     */
   private String fLinkPhone;

    /**
     * 联系人
     */
   private String fContact;

    /**
     * 仓库
     */
   private String fStock;

    /**
     * 备注
     */
   private String fLogNote;

    /**
     * 银行账号
     */
   private Map fBankAccount;

    /**
     * 收款方式
     */
   private String fCollectType;

    /**
     * 结算方式
     */
   private Map fSetType;

    /**
     * 表体
     */
   private List<PileTradePushTableBodyRequest> pushTableBodyRequestList;

    /**
     * 登录token
     */
   private String loginToken;

    /**
     * 是否使用线程池
     */
   private Boolean threadPool;
}
