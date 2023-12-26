package com.wanmi.sbc.goods.api.request.info;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsSpecDetailVO;
import com.wanmi.sbc.goods.bean.vo.GoodsSpecVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 特价商品特价类
 *  @author chenjun
 */
@ApiModel
@Data
public class SpecialGoodsAddRequest implements Serializable {

    private static final long serialVersionUID = 7092202291526014226L;

    private List<GoodsInfoVO> goodsInfoVOS;

    private List<GoodsVO> goodsVOS;

    private List<GoodsSpecVO> goodsSpecVOS;

    private List<GoodsSpecDetailVO> goodsSpecDetailVOS;
}
