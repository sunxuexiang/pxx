package com.wanmi.sbc.setting.api.response.logisticscompany;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.LogisticsBaseSiteVO;
import io.swagger.annotations.ApiModel;
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
public class LogisticsBaseSitePageResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private MicroServicePage<LogisticsBaseSiteVO> logisticsBaseSiteVOPage;
}
