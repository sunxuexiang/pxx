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
 * <p>根据id查询任意（包含已删除）天气设置信息response</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherSwitchByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 天气设置信息
     */
    @ApiModelProperty(value = "天气设置信息")
    private WeatherSwitchVO weatherSwitchVO;
}
