package com.wanmi.sbc.goods.api.response.cate;

import com.wanmi.sbc.goods.bean.vo.GoodsCateNewVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
public class GoodsCateListNewResponse implements Serializable {

    private static final long serialVersionUID = 3230253303501411675L;

    @ApiModelProperty(value = "商品类目")
    private List<GoodsCateNewVO> goodsCateVOList;
}
