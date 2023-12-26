package com.wanmi.sbc.goods.api.response.goodstypeconfig;

import com.wanmi.sbc.goods.bean.vo.MerchantRecommendTypeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>分类推荐分类列表结果</p>
 * @author sgy
 * @date 2019-09-07 10:53:36
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantTypeConfigListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分类推荐分类列表结果
     */
    @ApiModelProperty(value = "分类推荐分类列表结果")
    private List<MerchantRecommendTypeVO> merchantRecommendTypeVOList;
}
