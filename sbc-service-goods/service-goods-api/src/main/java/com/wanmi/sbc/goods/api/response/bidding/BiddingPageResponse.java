package com.wanmi.sbc.goods.api.response.bidding;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.BiddingVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>竞价配置分页结果</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiddingPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 竞价配置分页结果
     */
    @ApiModelProperty(value = "竞价配置分页结果")
    private MicroServicePage<BiddingVO> biddingVOPage;
}
