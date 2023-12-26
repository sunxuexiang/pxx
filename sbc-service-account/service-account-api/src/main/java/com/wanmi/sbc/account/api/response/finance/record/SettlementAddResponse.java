package com.wanmi.sbc.account.api.response.finance.record;

import com.wanmi.sbc.account.bean.vo.SettlementVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 结算新增响应请求
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class SettlementAddResponse extends SettlementVO implements Serializable {

    private static final long serialVersionUID = -5120647741604865530L;
}
