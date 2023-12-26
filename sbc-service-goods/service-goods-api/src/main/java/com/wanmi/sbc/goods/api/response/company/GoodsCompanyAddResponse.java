package com.wanmi.sbc.goods.api.response.company;

import com.wanmi.sbc.goods.bean.dto.GoodsBrandDTO;
import com.wanmi.sbc.goods.bean.vo.GoodsCompanyVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 厂商新增响应
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsCompanyAddResponse extends GoodsCompanyVO {

    private static final long serialVersionUID = 8253508996955495630L;
}
