package com.wanmi.sbc.setting.api.response.weatherswitch;

import com.wanmi.sbc.setting.bean.vo.WeatherSwitchVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>天气设置修改结果</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherSwitchModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的天气设置信息
     */
    @ApiModelProperty(value = "已修改的天气设置信息")
    private WeatherSwitchVO weatherSwitchVO;
}
