package com.wanmi.sbc.goods.api.response.catebrandsortrel;

import com.wanmi.sbc.goods.bean.vo.CateBrandSortRelVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * <p>类目品牌排序表列表结果</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CateBrandSortRelTemplateResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 类目品牌排序表列表结果
     */
    @ApiModelProperty(value = "导出文件")
    private String template;
}
