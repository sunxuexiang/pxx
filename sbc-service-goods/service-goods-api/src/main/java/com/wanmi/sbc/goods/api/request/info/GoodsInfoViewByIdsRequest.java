package com.wanmi.sbc.goods.api.request.info;

import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 根据商品SKU编号查询请求
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoViewByIdsRequest implements Serializable {

    private static final long serialVersionUID = -2265501195719873212L;

    /**
     * 批量SKU编号
     */
    @ApiModelProperty(value = "批量SKU编号")
    private List<String> goodsInfoIds;

    /**
     * 批量拆箱SKU编号
     */
    @ApiModelProperty(value = "批量拆箱SKU编号")
    private List<Long> devanningIds;


    /**
     * 是否需要显示规格明细
     * 0:否,1:是
     */
    @ApiModelProperty(value = "是否需要显示规格明细", dataType = "com.wanmi.sbc.common.enums.DefaultFlag")
    private Integer isHavSpecText;

    /**
     * 店铺ID
     */
    @ApiModelProperty("店铺ID")
    private Long storeId;

    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除")
    private DeleteFlag deleteFlag;

    /**
     * 分仓的Id
     */
    @ApiModelProperty(value = "是否删除")
    private Long wareId;

    /**
     * 仓库的编码
     */
    @ApiModelProperty(value = "仓库的编码")
    private String wareHouseCode;

    @ApiModelProperty(value = "是否匹配到仓")
    private Boolean matchWareHouseFlag;

}
