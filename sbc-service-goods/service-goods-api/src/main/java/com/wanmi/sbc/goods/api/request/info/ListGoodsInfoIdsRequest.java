package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 列出满足条件的商品id集合请求
 * Created by chenchang on 2022/9/22.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListGoodsInfoIdsRequest  implements Serializable {

    private static final long serialVersionUID = -4773268399434345893L;

    @ApiModelProperty(value = "仓库Id")
    @NotNull(message = "仓库Id不能为空")
    private Long wareId;

    @ApiModelProperty(value = "模糊条件-erp编码")
    private String likeErpNo;

    @ApiModelProperty(value = "模糊条件-商品名称")
    private String likeGoodsName;

    @ApiModelProperty(value = "分类编号")
    private Long cateId;

    @ApiModelProperty(value = "批量商品分类")
    private List<Long> cateIds;

    @ApiModelProperty(value = "品牌编号")
    private Long brandId;

    @ApiModelProperty(value = "批量品牌编号")
    private List<Long> brandIds;

    @ApiModelProperty(value = "参与囤货活动的商品id集合", hidden = true)
    private List<String> goodsInfoIds;
}
