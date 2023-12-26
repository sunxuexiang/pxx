package com.wanmi.sbc.goods.api.response.bidding;

import com.wanmi.sbc.goods.bean.vo.BiddingVO;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>根据id查询任意（包含已删除）竞价配置信息response</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiddingByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 竞价配置信息
     */
    @ApiModelProperty(value = "竞价配置信息")
    private BiddingVO biddingVO;

    /**
     * 商品的分类
     */
    @ApiModelProperty(value = "商品的分类")
    private List<GoodsCateVO> goodsCateVOS;

    /**
     * 商品的品牌
     */
    @ApiModelProperty(value = "商品的品牌")
    private List<GoodsBrandVO> goodsBrandVOS;
}
