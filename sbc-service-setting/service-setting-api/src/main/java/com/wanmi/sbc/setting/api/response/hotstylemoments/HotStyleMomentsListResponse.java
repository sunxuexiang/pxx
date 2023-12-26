package com.wanmi.sbc.setting.api.response.hotstylemoments;

import com.wanmi.sbc.setting.bean.vo.HotStyleMomentsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 爆款时刻列表查询响应实体类
 * @author: XinJiang
 * @time: 2022/5/9 21:54
 */
@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class HotStyleMomentsListResponse implements Serializable {

    private static final long serialVersionUID = -7097363116009942933L;

    /**
     * 爆款时刻列表
     */
    @ApiModelProperty(value = "爆款时刻列表")
    private List<HotStyleMomentsVO> hotStyleMomentsVOS;
}
