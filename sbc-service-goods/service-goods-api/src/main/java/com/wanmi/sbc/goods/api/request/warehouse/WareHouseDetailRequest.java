package com.wanmi.sbc.goods.api.request.warehouse;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * com.wanmi.sbc.goods.api.request.goodscate.GoodsCateListRequest
 * 根据条件查询商品分类列表信息请求对象
 * @author lipeng
 * @dateTime 2018/11/1 下午3:25
 */
@ApiModel
@Data
public class WareHouseDetailRequest extends BaseQueryRequest  {

    /**
     * 分类编号
     */
    @ApiModelProperty(value = "仓库ID")
    private Long wareId;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记", dataType = "com.wanmi.sbc.common.enums.DeleteFlag")
    private Integer delFlag;

    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称")
    private String wareName;

}
