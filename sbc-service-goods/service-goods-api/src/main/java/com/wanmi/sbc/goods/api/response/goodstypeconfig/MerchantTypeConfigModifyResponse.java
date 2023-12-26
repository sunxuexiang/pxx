package com.wanmi.sbc.goods.api.response.goodstypeconfig;

import com.wanmi.sbc.goods.bean.vo.MerchantRecommendTypeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>分类推荐分类修改结果</p>
 * @author sgy
 * @date 2023-06-07 10:53:36
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantTypeConfigModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的分类推荐分类信息
     */
    @ApiModelProperty(value = "已修改的分类推荐分类信息")
    private MerchantRecommendTypeVO recommendTypeVO;
}
