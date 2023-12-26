package com.wanmi.sbc.setting.api.response;

import com.wanmi.sbc.setting.bean.vo.NewMenuInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 二三级菜单返回集合
 * @author hudong
 * @date  2023-06-30
 */
@ApiModel
@Data
public class TwoAndThreeMenuInfoListResponse implements Serializable {
    private static final long serialVersionUID = 4734014779315554505L;
    /**
     * 菜单列表
     */
    @ApiModelProperty(value = "菜单列表")
    private List<NewMenuInfoVO> children = new ArrayList<>();
}
