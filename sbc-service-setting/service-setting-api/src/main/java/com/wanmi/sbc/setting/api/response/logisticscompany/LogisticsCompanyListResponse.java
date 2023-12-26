package com.wanmi.sbc.setting.api.response.logisticscompany;

import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>物流公司列表结果</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsCompanyListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 物流公司列表结果
     */
    @ApiModelProperty(value = "物流公司列表结果")
    private List<LogisticsCompanyVO> logisticsCompanyVOList;
}
