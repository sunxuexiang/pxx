package com.wanmi.sbc.setting.api.response.logisticscompany;

import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
public class LogisticsCompanyMobileListResponse implements Serializable {


    private LogisticsCompanyVO logisticsInfoVO;

}
