package com.wanmi.sbc.goods.response;

import com.wanmi.sbc.es.elastic.EsGoods;
import com.wanmi.sbc.es.elastic.response.EsGoodsLimitBrandShelflifeResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author shiGuangYi
 * @createDate 2023-07-20 16:57
 * @Description: 首页全局检索
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "商家分类结果")
public class IndexAllSearchResponse implements Serializable {

    private static final long serialVersionUID = 4713958133849159181L;

    @ApiModelProperty(value = "检索返回类型（0商品 1商家）")
    private int searchType;
    @ApiModelProperty(value = "商家信息")
    private List<GoodsMallPlatformSupplierVO> suppliers;
    @ApiModelProperty(value = "商城分类列表")
    private List<GoodsMallPlatformSupplierTabVO> mallTabs;
    /**
     * 索引SKU
     */
    @ApiModelProperty(value = "SPU")
    Page<EsGoods> esGoods = new PageImpl<>(new ArrayList<>());
    @ApiModelProperty(value = "产品")
    private EsGoodsLimitBrandShelflifeResponse  goodsLimitBrandShelflifeResponse;





}

