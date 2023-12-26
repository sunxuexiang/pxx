package com.wanmi.sbc.setting.api.response.activityconfig;

import com.wanmi.sbc.setting.bean.vo.ActivityConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>根据id查询任意（包含已删除）导航配置信息response</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityConfigByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 导航配置信息
     */
    @ApiModelProperty(value = "导航配置信息")
    private ActivityConfigVO activityConfigVO;
}
