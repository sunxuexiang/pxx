package com.wanmi.sbc.setting.api.response.logisticscompany;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyLineVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @desc  物流公司线路列表结果
 * @author shiy  2023/11/7 9:46
*/
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsCompanyLinePageResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物流公司线路列表结果")
    private MicroServicePage<LogisticsCompanyLineVO> logisticsCompanyLineVOPage;
}
