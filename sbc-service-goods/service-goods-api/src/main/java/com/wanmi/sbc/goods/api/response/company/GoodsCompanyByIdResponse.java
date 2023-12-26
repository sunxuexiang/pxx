package com.wanmi.sbc.goods.api.response.company;

import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCompanyVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 品牌查询响应
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsCompanyByIdResponse extends GoodsCompanyVO implements Serializable {

    private static final long serialVersionUID = -6942228033110682924L;
}
