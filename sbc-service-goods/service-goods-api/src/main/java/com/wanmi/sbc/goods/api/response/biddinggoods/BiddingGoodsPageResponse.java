package com.wanmi.sbc.goods.api.response.biddinggoods;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.BiddingGoodsVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>竞价商品分页结果</p>
 * @author baijz
 * @date 2020-08-05 16:34:44
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiddingGoodsPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 竞价商品分页结果
     */
    @ApiModelProperty(value = "竞价商品分页结果")
    private MicroServicePage<BiddingGoodsVO> biddingGoodsVOPage;
}
