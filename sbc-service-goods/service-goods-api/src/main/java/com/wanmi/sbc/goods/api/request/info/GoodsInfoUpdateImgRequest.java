package com.wanmi.sbc.goods.api.request.info;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 修改图片地址
 * @author: XinJiang
 * @time: 2022/4/9 17:02
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoUpdateImgRequest implements Serializable {

    private static final long serialVersionUID = 325509871514196432L;

    private List<GoodsInfoVO> goodsInfoVOList;
}
