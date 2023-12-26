package com.wanmi.sbc.goods.api.response.goods;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询商品全部信息
 * */
@ApiModel
@Data
public class GoodsSpuResponse implements Serializable {

    private static final long serialVersionUID = 01217231750135336752320L;

     private List<GoodsViewByIdResponse>  spuList;

}
