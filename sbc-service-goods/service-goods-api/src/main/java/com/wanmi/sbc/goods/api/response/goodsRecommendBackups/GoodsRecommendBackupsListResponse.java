package com.wanmi.sbc.goods.api.response.goodsRecommendBackups;

import com.wanmi.sbc.goods.bean.vo.GoodsRecommendBackupsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p> 备份数据</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:22
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendBackupsListResponse implements Serializable {

    private static final long serialVersionUID = -4849464110380717726L;

    /**
     * 备份数据
     */
    @ApiModelProperty(value = "备份数据")
    private List<GoodsRecommendBackupsVO> goodsRecommendBackupsVOList;
}

