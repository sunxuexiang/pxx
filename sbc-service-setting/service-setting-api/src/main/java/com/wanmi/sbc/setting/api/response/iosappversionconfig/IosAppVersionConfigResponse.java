package com.wanmi.sbc.setting.api.response.iosappversionconfig;

import com.wanmi.sbc.setting.bean.vo.IosAppVersionConfigVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>IOS基础服务分页结果</p>
 * @author zhou.jiang
 * @date 2021-09-15
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IosAppVersionConfigResponse {
    private List<IosAppVersionConfigVO> iosAppVersionConfigVOS;
}
