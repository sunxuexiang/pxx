package com.wanmi.sbc.goods.api.response.company;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCompanyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 品牌分页查询响应
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsCompanyPageResponse implements Serializable {

    private static final long serialVersionUID = -6942228033110682924L;

    /**
     * 厂商分页列表
     */
    @ApiModelProperty(value = "厂商分页列表")
    private MicroServicePage<GoodsCompanyVO> goodsCompanyPage;
}
