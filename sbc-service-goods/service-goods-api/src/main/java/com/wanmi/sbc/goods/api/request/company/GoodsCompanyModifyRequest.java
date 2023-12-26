package com.wanmi.sbc.goods.api.request.company;

import com.wanmi.sbc.goods.bean.dto.GoodsBrandDTO;
import com.wanmi.sbc.goods.bean.vo.GoodsCompanyVO;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 厂商修改
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
public class GoodsCompanyModifyRequest extends GoodsCompanyVO {

    private static final long serialVersionUID = 6755281517285771010L;
}
