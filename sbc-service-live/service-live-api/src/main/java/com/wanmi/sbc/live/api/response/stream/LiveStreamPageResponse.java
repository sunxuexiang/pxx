package com.wanmi.sbc.live.api.response.stream;

import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
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
public class LiveStreamPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 直播分页列表结果
     */
    @ApiModelProperty(value = "直播分页列表结果")
    public List<LiveStreamVO> content;

    @ApiModelProperty(value = "直播官方直播间")
    public LiveStreamVO mainContent;

    public boolean first; // required
    public boolean last; // required
    public int number; // required
    public int numberOfElements; // required
    public int size; // required
    public int totalElements; // required
    public int totalPages; // required
}
