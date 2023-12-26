package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * 城市编号
 *
 * @author yitang
 * @version 1.0
 */
@ApiModel
@Setter
@Getter
public class RegionVO {
    /**
     * 编号
     */
    private Long id;

    /**
     * 城市名称
     */
    private String name;

    /**
     * 上一级编号
     */
    private Long parentId;
}
