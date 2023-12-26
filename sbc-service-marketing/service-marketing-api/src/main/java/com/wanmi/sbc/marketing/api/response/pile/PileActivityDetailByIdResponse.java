package com.wanmi.sbc.marketing.api.response.pile;


import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 囤货活动详情
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PileActivityDetailByIdResponse implements Serializable {

    private static final long serialVersionUID = 5931701650280668507L;

    @ApiModelProperty(value = "囤货活动")
    private PileActivityVO pileActivity;
}
