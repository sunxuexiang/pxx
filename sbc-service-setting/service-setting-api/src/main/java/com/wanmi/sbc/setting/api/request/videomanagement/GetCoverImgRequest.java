package com.wanmi.sbc.setting.api.request.videomanagement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCoverImgRequest {
    /**
     * 素材地址
     */
    @ApiModelProperty(value = "素材地址")
    private String artworkUrl;
}
