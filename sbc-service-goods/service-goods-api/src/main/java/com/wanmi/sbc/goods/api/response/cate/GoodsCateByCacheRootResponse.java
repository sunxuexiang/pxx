package com.wanmi.sbc.goods.api.response.cate;

import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * com.wanmi.sbc.goods.api.response.goodscate.GoodsCateByCacheResponse
 * 通过缓存获取商品分类信息响应对象
 * @author lipeng
 * @dateTime 2018/11/2 上午9:20
 */
@ApiModel
@Data
public class GoodsCateByCacheRootResponse implements Serializable {

    private static final long serialVersionUID = -5871368955686770930L;

    @ApiModelProperty(value = "分类实体类")
    private GoodsCateVO cateVO;
}
