package com.wanmi.sbc.es.elastic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.es.elastic.model.root.*;
import com.wanmi.sbc.goods.bean.vo.GoodsLabelVO;
import com.wanmi.sbc.goods.bean.vo.GoodsPropDetailRelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingForEndVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jeffrey
 * @create 2021-08-04
 */
@Data
public class EsGoodsShelflife {

    private String id;

    /**
     * 转化为小写
     */
    private String lowGoodsName;

//    /**
//     * SKU信息
//     */
//    private List<GoodsInfoNest> goodsInfos;

    /**
     * SKU信息(包含保质期)
     */
    private List<GoodsInfoNestShelflife> goodsInfos;


    /**
     * 商品分类信息
     */
    private GoodsCateNest goodsCate;

    /**
     * 商品品牌信息
     */
    private GoodsBrandNest goodsBrand;

    /**
     * 上下架时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime addedTime;

    /**
     * SKU相关规格
     */
    private List<GoodsInfoSpecDetailRelNest> specDetails;

    /**
     * 多对多关系，SPU相关属性
     */
    private List<GoodsPropDetailRelVO> propDetails;

    /**
     * 等级价数据
     */
    private List<GoodsLevelPriceNest> goodsLevelPrices = new ArrayList<>();

    /**
     * 客户价数据
     */
    private List<GoodsCustomerPriceNest> customerPrices = new ArrayList<>();

    /**
     * 签约开始日期
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime contractStartDate;

    /**
     * 签约结束日期
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime contractEndDate;

    /**
     * 店铺状态 0、开启 1、关店
     */
    private Integer storeState;

    /**
     * 禁售状态
     */
    private Integer forbidStatus;

    /**
     * 审核状态
     */
    private Integer auditStatus;

    /**
     * 多对多关系，多个店铺分类编号
     */
    private List<Long> storeCateIds;

    /**
     * 营销信息
     */
    private List<MarketingForEndVO> marketingList = new ArrayList<>();

    /**
     * 分销商品状态，配合分销开关使用
     */
    private Integer distributionGoodsStatus;

    /**
     * 商品评论数
     */
    private Long goodsEvaluateNum;

    /**
     * 商品收藏量
     */
    private Long goodsCollectNum;

    /**
     * 商品销量
     */
    private Long goodsSalesNum;

    /**
     * 商品好评数量
     */
    private Long goodsFavorableCommentNum;

    /**
     * 商品好评率
     */
    private Long goodsFeedbackRate;

    /**
     * 是否是特价商品
     */
    private Integer goodsInfoType;

    /**
     * 商品排序序号
     */
    @ApiModelProperty(value = "商品排序序号")
    private Integer goodsSeqNum;

    /**
     * 商品副标题
     */
    @ApiModelProperty(value = "商品fu标题")
    private String goodsSubtitle;

    /**
     * 商品副标题new
     */
    @ApiModelProperty(value = "商品fu标题new")
    private String goodsSubtitleNew;

    @ApiModelProperty(value = "最小单价")
    private BigDecimal minimumPrice =BigDecimal.ZERO;

    /**
     * 商品绑定的规格数据
     */
    @ApiModelProperty(value = "商品绑定的规格数据")
    private List<GoodsLabelVO> goodsLabels = new ArrayList<>();


}
