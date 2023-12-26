package com.wanmi.sbc.goods.api.response.catebrandsortrel;

import com.wanmi.sbc.goods.bean.vo.CateBrandSortRelVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>类目品牌排序表修改结果</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CateBrandSortRelModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的类目品牌排序表信息
     */
    @ApiModelProperty(value = "已修改的类目品牌排序表信息")
    private CateBrandSortRelVO cateBrandSortRelVO;
}
