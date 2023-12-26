package com.wanmi.sbc.goods.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-07-11 19:27
 **/
@Data
public class RecommendGuessLoveForStoreRequest implements Serializable {
    private static final long serialVersionUID = -1707542923416661995L;

    @ApiModelProperty(value = "店铺id")
    private Long storeId;
}
