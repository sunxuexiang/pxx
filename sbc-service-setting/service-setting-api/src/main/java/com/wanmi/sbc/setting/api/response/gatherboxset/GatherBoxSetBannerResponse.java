package com.wanmi.sbc.setting.api.response.gatherboxset;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatherBoxSetBannerResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 散批首页banner
     */
    private String banner;
}
