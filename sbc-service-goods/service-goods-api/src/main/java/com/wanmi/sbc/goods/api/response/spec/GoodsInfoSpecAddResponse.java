package com.wanmi.sbc.goods.api.response.spec;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoSpecDetailRelVO;
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
public class GoodsInfoSpecAddResponse implements Serializable {


    private static final long serialVersionUID = 6435530772875360983L;
    @ApiModelProperty(value = "商品规格list")
    private List<GoodsSpecVO> goodsSpecVOS;


}
