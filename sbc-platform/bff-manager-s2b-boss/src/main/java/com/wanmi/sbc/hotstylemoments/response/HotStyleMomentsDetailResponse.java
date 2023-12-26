package com.wanmi.sbc.hotstylemoments.response;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.setting.bean.vo.HotStyleMomentsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 爆款时刻详情响应实体类
 * @author: XinJiang
 * @time: 2022/5/10 15:17
 */
@Data
@ApiModel
public class HotStyleMomentsDetailResponse implements Serializable {

    private static final long serialVersionUID = -5975389760628502101L;

    /**
     * 爆款时刻信息
     */
    @ApiModelProperty(value = "爆款时刻信息")
    private HotStyleMomentsVO hotStyleMomentsVO;

    /**
     * sku集合
     */
    @ApiModelProperty(value = "sku集合")
    private List<GoodsInfoVO> goodsInfos;

    /**
     * spu集合
     */
    @ApiModelProperty(value = "spu集合")
    private List<GoodsVO> goods;
}
