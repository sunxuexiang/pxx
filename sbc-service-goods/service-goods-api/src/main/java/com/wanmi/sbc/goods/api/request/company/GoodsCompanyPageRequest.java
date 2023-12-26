package com.wanmi.sbc.goods.api.request.company;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * 品牌分页查询请求
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsCompanyPageRequest extends BaseQueryRequest {

    private static final long serialVersionUID = -6942228033110682924L;

    /**
     * and 精准查询，品牌名称
     */
    @ApiModelProperty(value = "like 模糊查询，厂商名称")
    private String companyName;

    /**
     * 禁用标记
     */
    @ApiModelProperty(value = "禁用标记")
    private Integer status;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记")
    private Integer delFlag;
}
