package com.wanmi.sbc.returnorder.api.response.historylogisticscompany;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.returnorder.bean.vo.HistoryLogisticsCompanyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>配送到家分页结果</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryLogisticsCompanyPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 配送到家分页结果
     */
    @ApiModelProperty(value = "配送到家分页结果")
    private MicroServicePage<HistoryLogisticsCompanyVO> historyLogisticsCompanyVOPage;
}
