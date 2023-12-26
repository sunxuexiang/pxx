package com.wanmi.sbc.returnorder.api.response.historylogisticscompany;

import com.wanmi.sbc.returnorder.bean.vo.HistoryLogisticsCompanyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>配送到家修改结果</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryLogisticsCompanyModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的配送到家信息
     */
    @ApiModelProperty(value = "已修改的配送到家信息")
    private HistoryLogisticsCompanyVO historyLogisticsCompanyVO;
}
