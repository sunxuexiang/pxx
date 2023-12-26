package com.wanmi.sbc.goods.api.request.marketing;

import com.wanmi.sbc.goods.bean.dto.GoodsMarketingDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>商品营销同步请求</p>
 * author: yang
 * Date: 2020-12-26
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsMarketingSyncRequest implements Serializable {

    private static final long serialVersionUID = 8976853170615340770L;

    /**
     * 用户编号
     */
    @ApiModelProperty(value = "用户编号")
    private String customerId;

    /**
     * sku对营销编号
     */
    @ApiModelProperty(value = "sku对应的营销编号")
    Map<String, List<Long>> marketingIdsMap = new HashMap<>();

}
