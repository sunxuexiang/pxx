package com.wanmi.sbc.goods.api.response.goods;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoAttributeVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询商品全部信息
 * */
@ApiModel
@Data
public class GoodsSpuAttributeResponse implements Serializable {

    private static final long serialVersionUID = -6641896293423917872L;

    private List<GoodsInfoAttributeVO> attributeList;


}
