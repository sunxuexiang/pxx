package com.wanmi.sbc.goods.api.request.company;

import com.wanmi.sbc.goods.bean.dto.GoodsBrandDTO;
import com.wanmi.sbc.goods.bean.vo.GoodsCompanyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 品牌更新请求
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
public class GoodsCompanySaveRequest implements Serializable {

    private static final long serialVersionUID = 1304491163008406897L;

    /**
     * 商品品牌信息
     */
    @ApiModelProperty(value = "商品品牌信息")
    private GoodsCompanyVO goodsCompany;
}
