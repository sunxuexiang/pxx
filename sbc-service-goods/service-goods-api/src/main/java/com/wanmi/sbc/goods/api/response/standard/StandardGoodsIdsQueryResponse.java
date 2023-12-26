package com.wanmi.sbc.goods.api.response.standard;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 商品库查询请求
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardGoodsIdsQueryResponse implements Serializable {

    private static final long serialVersionUID = 6670602547853630698L;
    /**
     * goodsIds
     */
    private List<String> goodsIds ;
}
