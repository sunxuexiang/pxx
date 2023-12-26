package com.wanmi.sbc.goods.api.response.spec;

import com.wanmi.sbc.goods.bean.vo.GoodsSpecDetailVO;
import com.wanmi.sbc.goods.bean.vo.GoodsSpecVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wanggang
 * @createDate: 2018/11/13 14:59
 * @version: 1.0
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsInfoSpecDetailAddResponse implements Serializable {


    private static final long serialVersionUID = 396273630903909049L;
    @ApiModelProperty(value = "商品规格值list")
    private List<GoodsSpecDetailVO> goodsSpecDetailVOS;


}
