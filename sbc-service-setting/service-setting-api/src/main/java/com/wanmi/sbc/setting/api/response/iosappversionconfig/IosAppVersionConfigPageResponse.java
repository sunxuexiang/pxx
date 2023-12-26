package com.wanmi.sbc.setting.api.response.iosappversionconfig;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.IosAppVersionConfigVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: ios版本配置信息分页
 * @author: jiangxin
 * @create: 2021-09-23 15:40
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IosAppVersionConfigPageResponse implements Serializable {

    private static final long serialVersionUID = 2489602277554522717L;

    /**
     * ios版本配置分页结果
     */
    @ApiModelProperty(value = "ios版本配置分页结果")
    private MicroServicePage<IosAppVersionConfigVO> iosVersionPages;

}
