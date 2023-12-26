package com.wanmi.sbc.live.api.response.bag;

import com.wanmi.sbc.live.bean.vo.LiveBagVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveBagListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "直播间福袋列表")
    private List<LiveBagVO> liveBagVOS;
}
