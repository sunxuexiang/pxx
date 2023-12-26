package com.wanmi.sbc.order.api.response.historylogisticscompany;

import com.wanmi.sbc.order.bean.vo.HistoryLogisticsCompanyVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>根据id查询任意（包含已删除）物流公司历史记录信息response</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryLogisticsCompanyByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 物流公司历史记录信息
     */
    @ApiModelProperty(value = "物流公司历史记录信息")
    private HistoryLogisticsCompanyVO historyLogisticsCompanyVO;
}
