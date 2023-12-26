package com.wanmi.sbc.goods.api.request.GoodsRecommendBackups;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 新增商品推荐请求对象
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsRecommendBackupsQueryRequest extends BaseQueryRequest implements Serializable {

    private static final long serialVersionUID = 215195317836066025L;

    @ApiModelProperty(value = "用户Id")
    private String customerId;

    @ApiModelProperty(value = "公司Id")
    private Long companyId;

}
