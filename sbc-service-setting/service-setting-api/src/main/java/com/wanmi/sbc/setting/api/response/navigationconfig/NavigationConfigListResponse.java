package com.wanmi.sbc.setting.api.response.navigationconfig;

import com.wanmi.sbc.setting.bean.vo.NavigationConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>导航配置列表结果</p>
 * @author lvheng
 * @date 2021-04-12 14:46:18
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationConfigListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 导航配置列表结果
     */
    @ApiModelProperty(value = "导航配置列表结果")
    private List<NavigationConfigVO> navigationConfigVOList;
}
