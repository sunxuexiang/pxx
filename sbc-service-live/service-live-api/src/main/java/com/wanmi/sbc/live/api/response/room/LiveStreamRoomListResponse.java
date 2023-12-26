package com.wanmi.sbc.live.api.response.room;

import com.wanmi.sbc.live.bean.vo.LiveRoomVO;
import io.swagger.annotations.ApiModel;
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
public class LiveStreamRoomListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 直播间列表
     */
    private List<LiveRoomVO> liveRoomVOList;
}
