package com.wanmi.sbc.setting.api.response.hotstylemoments;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.HotStyleMomentsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description: 爆款时刻分页查询响应实体类
 * @author: XinJiang
 * @time: 2022/5/9 21:56
 */
@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class HotStyleMomentsPageResponse implements Serializable {

    private static final long serialVersionUID = 7902816714758757228L;

    /**
     * 分页数据
     */
    @ApiModelProperty(value = "分页数据")
    private MicroServicePage<HotStyleMomentsVO> hotStyleMomentsPage;
}
