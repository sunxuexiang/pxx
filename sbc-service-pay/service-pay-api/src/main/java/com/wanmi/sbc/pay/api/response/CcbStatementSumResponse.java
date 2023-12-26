package com.wanmi.sbc.pay.api.response;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.pay.bean.vo.CcbStatementSumVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 建行对账单汇总返回
 * @author hudong
 * 2023-09-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CcbStatementSumResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对账单汇总分页结果
     */
    @ApiModelProperty(value = "对账单汇总分页结果")
    private MicroServicePage<CcbStatementSumVO> ccbStatementSumVOPage;

}
