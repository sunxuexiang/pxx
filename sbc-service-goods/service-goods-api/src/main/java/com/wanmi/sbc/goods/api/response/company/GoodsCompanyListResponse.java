package com.wanmi.sbc.goods.api.response.company;
import com.wanmi.sbc.goods.bean.vo.GoodsCompanyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsCompanyListResponse implements Serializable {

    private static final long serialVersionUID = -6942228033110682924L;

    /**
     * 厂商列表
     */
    @ApiModelProperty(value = "厂商列表")
    private List<GoodsCompanyVO> goodsCompanyVOList;
}
