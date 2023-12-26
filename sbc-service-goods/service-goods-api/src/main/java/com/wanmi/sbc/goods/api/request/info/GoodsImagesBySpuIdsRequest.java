package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @description: 商品图片请求类
 * @author: XinJiang
 * @time: 2022/4/9 16:25
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsImagesBySpuIdsRequest implements Serializable {

    private static final long serialVersionUID = -5266130923842349330L;

    @NotNull
    private List<String> spuIds;
}
