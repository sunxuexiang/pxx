package com.wanmi.sbc.setting.api.response.doorpick;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.DoorPickConfigVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoorPickConfigPageResponse implements Serializable {

    private static final long serialVersionUID = -209744202815448213L;
    /**
     * 公司信息列表
     */
    @ApiModelProperty(value = "上门自提地址")
    private MicroServicePage<DoorPickConfigVO> doorPickConfigVOMicroServicePage;
}
