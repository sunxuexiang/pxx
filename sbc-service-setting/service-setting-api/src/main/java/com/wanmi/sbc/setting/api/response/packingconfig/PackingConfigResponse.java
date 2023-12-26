package com.wanmi.sbc.setting.api.response.packingconfig;

import com.wanmi.sbc.setting.bean.vo.OnlineServiceVO;
import com.wanmi.sbc.setting.bean.vo.PackingConfigVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.EAN;

import java.io.Serializable;
import java.util.List;

/**
 * <p>onlineService新增结果</p>
 * @author lq
 * @date 2019-11-05 16:10:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackingConfigResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "包装配置信息")
    private PackingConfigVO packingConfigVO;
}
