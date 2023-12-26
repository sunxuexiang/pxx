package com.wanmi.sbc.live.api.response.room;

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
public class LiveRoomBrandIdsResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 品牌列表
     */
    private List<Long> brandIds;
}
