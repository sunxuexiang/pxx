package com.wanmi.sbc.goods.api.response.biddinggoods;

import com.wanmi.sbc.goods.bean.vo.BiddingGoodsVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>竞价商品列表结果</p>
 * @author baijz
 * @date 2020-08-05 16:34:44
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiddingGoodsListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 竞价商品列表结果
     */
    @ApiModelProperty(value = "竞价商品列表结果")
    private List<BiddingGoodsVO> biddingGoodsVOList;
}
