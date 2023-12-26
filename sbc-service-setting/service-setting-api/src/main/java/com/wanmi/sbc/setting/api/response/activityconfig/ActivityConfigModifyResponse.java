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
 * <p>导航配置修改结果</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityConfigModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的导航配置信息
     */
    @ApiModelProperty(value = "已修改的导航配置信息")
    private ActivityConfigVO activityConfigVO;
}
