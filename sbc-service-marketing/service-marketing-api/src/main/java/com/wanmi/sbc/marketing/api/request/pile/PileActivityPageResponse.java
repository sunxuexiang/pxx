package com.wanmi.sbc.marketing.api.request.pile;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p></p>
 * author: chenchang
 * Date: 2022-09-06
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PileActivityPageResponse implements Serializable {

    private static final long serialVersionUID = -8124186308817616783L;

    @ApiModelProperty(value = "囤活动信息分页列表")
    private MicroServicePage<PileActivityVO> pileActivityVOPage;
}
