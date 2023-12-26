package com.wanmi.sbc.pay.api.response;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.pay.bean.vo.CcbStatementDetailVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 建行对账单明细返回
 * @author hudong
 * 2023-09-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CcbStatementDetailResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对账单明细分页结果
     */
    @ApiModelProperty(value = "对账单明细分页结果")
    private MicroServicePage<CcbStatementDetailVO> ccbStatementDetailVOPage;

}
