package com.wanmi.sbc.goods.retailgoodsrecommend.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: 散批鲸喜推荐商品排序请求参数类
 * @author: XinJiang
 * @time: 2022/4/20 10:36
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BulkGoodsCommendSortRequest implements Serializable {

    private static final long serialVersionUID = 3751847599946450064L;

    /**
     * 推荐id
     */
    @NotBlank
    private String recommendId;

    /**
     * 排序顺序
     */
    @NotNull
    private Integer sortNum;
}
