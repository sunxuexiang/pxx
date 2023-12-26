package com.wanmi.sbc.pay.api.response;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.pay.bean.vo.CcbStatementDetVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 建行对账单分账明细返回
 * @author hudong
 * 2023-09-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CcbStatementDetResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对账单分账明细分页结果
     */
    @ApiModelProperty(value = "对账单分账明细分页结果")
    private MicroServicePage<CcbStatementDetVO> ccbStatementDetVOPage;

}
