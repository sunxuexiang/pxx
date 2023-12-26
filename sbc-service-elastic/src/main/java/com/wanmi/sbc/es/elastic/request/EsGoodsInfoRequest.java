package com.wanmi.sbc.es.elastic.request;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * ES商品SKU请求
 * Created by daiyitian on 2017/3/24.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class EsGoodsInfoRequest {

    /**
     * 批量商品SkuID
     */
    @ApiModelProperty(value = "批量商品SkuID")
    private List<String> skuIds;

    /**
     * 公司信息ID
     */
    @ApiModelProperty(value = "公司信息ID")
    private Long companyInfoId;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    /**
     * spuId
     */
    @ApiModelProperty(value = "spuId")
    private String goodsId;

    /**
     * spuIds
     */
    @ApiModelProperty(value = "spuIds")
    private List<String> goodsIds;

    /**
     * 品牌ids
     */
    @ApiModelProperty(value = "品牌ids")
    private List<Long> brandIds;

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态", notes = "0：待审核 1：已审核 2：审核失败 3：禁售中")
    private CheckStatus auditStatus;

    private Integer addedFlag;

    private List<Long> storeIds;




    /**
     * 如果有范围进行初始化索引，无需删索引
     * @return true:clear index false:no
     */
    public boolean isClearEsIndex(){
        if(CollectionUtils.isNotEmpty(skuIds)
                || companyInfoId != null || storeId != null
                || StringUtils.isNotBlank(goodsId) || CollectionUtils.isNotEmpty(goodsIds)||CollectionUtils.isNotEmpty(brandIds) || CollectionUtils.isNotEmpty(storeIds)){
            return false;
        }
        return true;
    }
}
