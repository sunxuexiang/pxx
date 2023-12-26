package com.wanmi.sbc.marketing.api.response.pile;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p></p>
 * author: chenchang
 * Date: 2022-09-06
 */
@ApiModel
@Data
public class PileActivityDetailResponse implements Serializable {

    private static final long serialVersionUID = -2632254645824662407L;

    @ApiModelProperty(value = "囤货活动")
    private PileActivityVO pileActivity;

    @ApiModelProperty(value = "商品信息")
    private List<GoodsInfoVO> goodsInfoVOS;

}
