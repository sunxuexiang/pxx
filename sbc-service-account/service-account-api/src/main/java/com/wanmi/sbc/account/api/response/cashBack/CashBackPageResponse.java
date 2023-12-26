package com.wanmi.sbc.account.api.response.cashBack;

import com.wanmi.sbc.account.bean.vo.CashBackVO;
import com.wanmi.sbc.account.bean.vo.CustomerFundsVO;
import com.wanmi.sbc.common.base.MicroServicePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashBackPageResponse implements Serializable {

    @ApiModelProperty(value ="返现管理-返现打款列表")
    private MicroServicePage<CashBackVO> microServicePage;
}
