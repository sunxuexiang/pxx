package com.wanmi.sbc.setting.api.response.doorpick;

import com.wanmi.sbc.setting.bean.vo.DoorPickConfigVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoorPickConfigResponse implements Serializable {

    private static final long serialVersionUID = -209744202815448213L;

    @ApiModelProperty(value = "会员收货地址")
    private List<DoorPickConfigVO> doorPickConfigVOS = new ArrayList<>();
}
