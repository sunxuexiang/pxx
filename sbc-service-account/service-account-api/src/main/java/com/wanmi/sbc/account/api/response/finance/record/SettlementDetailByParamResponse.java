package com.wanmi.sbc.account.api.response.finance.record;

import com.wanmi.sbc.account.bean.vo.SettlementDetailVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>根据条件查询单条结算明细返回结构</p>
 * Created by of628-wenzhi on 2018-10-13-下午7:02.
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettlementDetailByParamResponse implements Serializable{
    private static final long serialVersionUID = -4542541344345887311L;

    /**
     * 结算明细 {@link SettlementDetailVO}
     */
    @ApiModelProperty(value = "结算明细")
    private SettlementDetailVO settlementDetailVO;
}
