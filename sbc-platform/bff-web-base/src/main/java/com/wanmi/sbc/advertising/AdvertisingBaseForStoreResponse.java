package com.wanmi.sbc.advertising;

import com.wanmi.sbc.goods.bean.vo.StoreCateResponseVO;
import com.wanmi.sbc.goods.bean.vo.StoreCateVO;
import com.wanmi.sbc.setting.bean.vo.AdvertisingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 获取有效首页广告位响应类
 * @author: XinJiang
 * @time: 2022/3/2 9:28
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdvertisingBaseForStoreResponse implements Serializable {

    /**
     * 首页广告位列表信息
     */
    @ApiModelProperty(value = "首页广告位列表信息")
    private List<AdvertisingVO> advertisingVOList;

    /**
     * 从缓存中获取商品分类信息列表
     */
    @ApiModelProperty(value = "从缓存中获取商品分类信息列表")
    private List<StoreCateVO> goodsCateVOS;

    /**
     * 推荐商品分类信息
     */
    @ApiModelProperty(value = "推荐商品分类信息")
    private List<StoreCateVO> recommendGoodsCate;

    /**
     * 超市大白鲸图标地址
     */
    @ApiModelProperty(value = "超市大白鲸图标地址")
    private String imageUrl;
}
