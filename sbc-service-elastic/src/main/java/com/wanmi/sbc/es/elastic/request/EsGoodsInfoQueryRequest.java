package com.wanmi.sbc.es.elastic.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.EsConstants;
import com.wanmi.sbc.es.elastic.request.dto.EsGoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;
import com.wanmi.sbc.goods.bean.vo.LiveGoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * 商品SKU查询请求
 * Created by daiyitian on 2017/3/24.
 */
@Data
@ApiModel
public class EsGoodsInfoQueryRequest extends BaseQueryRequest {

    /**
     * 未登录时,前端采购单缓存信息
     */
    @Valid
    @ApiModelProperty(value = "未登录时,前端采购单缓存信息")
    private List<EsGoodsInfoDTO> esGoodsInfoDTOList;

    /**
     * 批量商品ID
     */
    @ApiModelProperty(value = "批量商品ID")
    private List<String> goodsIds;

    private List<String> goodsIdsNot;

    /**
     * 直播商品对象
     */
    @ApiModelProperty(value = "直播商品对象")
    private List<LiveGoodsInfoVO> liveGoodsInfoVOS;

    /**
     * 批量SKU编号
     */
    @ApiModelProperty(value = "批量SKU编号")
    private List<String> goodsInfoIds;

    /**
     * 模糊条件-商品名称
     */
    @ApiModelProperty(value = "模糊条件-商品名称")
    private String likeGoodsName;

    /**
     * 商品条形码
     */
    @ApiModelProperty(value = "erp商品no")
    private String erpGoodsInfoNo;

    /**
     * 上下架状态
     */
    @ApiModelProperty(value = "上下架状态")
    private Integer addedFlag;

    /**
     * 商品分类 0,散装 1，定量
     */
    @ApiModelProperty(value = "商品分类 0,散装 1，定量")
    private Integer isScatteredQuantitative;

    /**
     * 分类编号
     */
    @ApiModelProperty(value = "分类编号")
    private Long cateId;

    /**
     * 一级分类编号
     */
    @ApiModelProperty(value = "分类编号")
    private Long cate1Id;

    /**
     * 二级分类编号
     */
    @ApiModelProperty(value = "分类编号")
    private Long cate2Id;

    /**
     * 三级分类编号
     */
    @ApiModelProperty(value = "分类编号")
    private Long cate3Id;

    /**
     * 称量类型
     */
    @ApiModelProperty(value = "称量类型")
    private Integer classifyType;


    /**
     * 批量分类编号
     */
    @ApiModelProperty(value = "批量分类编号")
    private List<Long> cateIds;

    /**
     * 批量店铺分类编号
     */
    @ApiModelProperty(value = "批量店铺分类编号")
    private List<Long> storeCateIds;

    /**
     * 批量品牌编号
     */
    @ApiModelProperty(value = "批量品牌编号")
    private List<Long> brandIds;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记")
    private Integer delFlag;

    /**
     * 库存状态
     * null:所有,1:有货,0:无货
     */
    @ApiModelProperty(value = "库存状态")
    private Integer stockFlag;

    /**
     * 排序标识
     * 0: 销量倒序->时间倒序->市场价倒序
     * 1:上架时间倒序->销量倒序->市场价倒序
     * 2:市场价倒序->销量倒序
     * 3:市场价升序->销量倒序
     * 4:销量倒序->市场价倒序
     * 5:评论数倒序->销量倒序->市场价倒序
     * 6:好评倒序->销量倒序->市场价倒序
     * 7:收藏倒序->销量倒序->市场价倒序
     */
    @ApiModelProperty(value = "排序标识")
    private Integer sortFlag;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID")
    private String customerId;

    /**
     * 客户等级
     */
    @ApiModelProperty(value = "客户等级")
    private Long customerLevelId;

    /**
     * 客户等级折扣
     */
    @ApiModelProperty(value = "客户等级折扣")
    private BigDecimal customerLevelDiscount;

    /**
     * 关键字，可能含空格
     */
    @ApiModelProperty(value = "关键字，可能含空格")
    private String keywords;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @ApiModelProperty(value = "商家类型")
    private Integer companyType;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    /**
     * 签约开始日期
     */
    @ApiModelProperty(value = "签约开始日期")
    private String contractStartDate;

    /**
     * 签约结束日期
     */
    @ApiModelProperty(value = "签约结束日期")
    private String contractEndDate;

    /**
     * 店铺状态 0、开启 1、关店
     */
    @ApiModelProperty(value = "店铺状态",dataType = "com.wanmi.sbc.customer.bean.enums.StoreState")
    private Integer storeState;

    /**
     * 禁售状态
     */
    @ApiModelProperty(value = "禁售状态")
    private Integer forbidStatus;

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态",dataType = "com.wanmi.sbc.goods.bean.enums.CheckStatus")
    private Integer auditStatus;

    /**
     * 聚合参数
     */
    @ApiModelProperty(value = "聚合参数")
    private List<AbstractAggregationBuilder> aggs = new ArrayList<>();

    /**
     * 排序参数
     */
    @ApiModelProperty(value = "排序参数")
    private List<SortBuilder> sorts = new ArrayList<>();

    /**
     * 精确查询-规格值参数
     */
    @ApiModelProperty(value = "精确查询-规格值参数")
    private List<EsSpecQueryRequest> specs = new ArrayList<>();

    /**
     * 营销Id
     */
    @ApiModelProperty(value = "营销Id")
    private Long marketingId;

    /**
     * 优惠券活动id
     */
    @ApiModelProperty(value = "优惠券活动id")
    private String activityId;

    /**
     * 指定商品赠鲸活动ID
     */
    @ApiModelProperty(value = "指定商品赠鲸活动ID")
    private String coinActivityId;

    /**
     * 是否需要反查分类
     */
    @ApiModelProperty(value = "是否需要反查分类",dataType = "com.wanmi.sbc.common.enums.BoolFlag")
    private boolean cateAggFlag;

    /**
     * 多个 属性与对应的属性值id列表
     */
    @ApiModelProperty(value = "多个 属性与对应的属性值id列表")
    private List<EsPropQueryRequest> propDetails = new ArrayList<>();

    @ApiModelProperty(value = "是否查询商品")
    private boolean isQueryGoods = false;

    @ApiModelProperty(value = "分销商品审核状态 0:普通商品 1:待审核 2:已审核通过 3:审核不通过 4:禁止分销")
    private Integer distributionGoodsAudit;


    @ApiModelProperty(value = "企业购商品审核状态 0:无状态 1:待审核 2:已审核通过 3:审核不通过 4:禁止分销")
    private Integer enterPriseAuditStatus;

    @ApiModelProperty(value = "隐藏已选分销商品,false:不隐藏，true:隐藏")
    private boolean hideSelectedDistributionGoods = Boolean.FALSE;

    @ApiModelProperty(value = "排除分销商品")
    private boolean excludeDistributionGoods = Boolean.FALSE;

    /**
     * 分销商品状态，配合分销开关使用
     */
    @ApiModelProperty(value = "分销商品状态，配合分销开关使用")
    private Integer distributionGoodsStatus;

    /**
     * 企业购商品过滤
     */
    @ApiModelProperty(value = "分销商品状态，配合分销开关使用")
    private Integer enterPriseGoodsStatus;

    /**
     * 批量分销商品SKU编号
     */
    @ApiModelProperty(value = "批量分销商品SKU编号")
    private List<String> distributionGoodsInfoIds;

    /**
     * 是否是特价商品
     */
    @ApiModelProperty(value = "是否是特价商品")
    private Integer goodsInfoType;

    /**
     * 匹配的分仓Id
     */
    @ApiModelProperty(value = "匹配的分仓Id")
    private Long wareId;

    /**
     * 匹配的分仓Id
     */
    @ApiModelProperty(value = "app端wareId(app端商品查询需要做限制)")
    private Long wareIdApp;

    /**
     * 是否为关键字查询
     */
    @ApiModelProperty(value = "是否为关键字查询")
    private Boolean isKeywords;

    /**
     * 是否为关键字查询
     */
    @ApiModelProperty(value = "是否能匹配仓")
    private Boolean matchWareHouseFlag;

    @ApiModelProperty(value = "是否根据品类绑定的品牌排序")
    private Boolean sortByCateBrand = false;

    @ApiModelProperty(value = "分类绑定的品牌数据")
    private List<Long> cateBindBrandIds;

    @ApiModelProperty(value = "购物车类型 0:囤货购物车 1:正常购物车 ")
    private Integer purchaseType;

    @ApiModelProperty(value = "购物车类型 0:囤货购物车 1:正常购物车 ")
    private Integer newPurchaseType;

    @ApiModelProperty(value = "请求图片标志")
    private Boolean imageFlag = Boolean.FALSE;
    /**
     * 商户Id
     */
    @ApiModelProperty(value = "商户Id")
    private Long companyInfoId;

    private String storeName;

    // 市场Id
    private Long marketId;

    // 店铺IDS
    private List<Long> storeIds;

    private Long mallId;

    private Boolean marketMallQuery;

    /**
     * 封装公共条件
     *
     * @return
     */
    public SearchQuery getRetailSearchCriteria() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withIndices(isQueryGoods ? EsConstants.DOC_RETAIL_GOODS_TYPE : EsConstants.DOC_RETAIL_GOODS_INFO_TYPE);
        builder.withQuery(this.getWhereCriteria());
//        System.out.println("where===>" + this.getWhereCriteria().toString());
        builder.withPageable(this.getPageable());
        if (CollectionUtils.isNotEmpty(sorts)) {
            sorts.forEach(builder::withSort);
        }
        if (CollectionUtils.isNotEmpty(aggs)) {
            aggs.forEach(builder::addAggregation);
        }
        return builder.build();
    }

    /**
     * 封装公共条件
     *
     * @return
     */
    public SearchQuery getSearchCriteria() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withIndices(isQueryGoods ? EsConstants.DOC_GOODS_TYPE : EsConstants.DOC_GOODS_INFO_TYPE);
//        builder.withTypes(isQueryGoods ? EsConstants.DOC_GOODS_TYPE : EsConstants.DOC_GOODS_INFO_TYPE);
        builder.withQuery(this.getWhereCriteria());
//        System.out.println("where===>" + this.getWhereCriteria().toString());
        builder.withPageable(this.getPageable());
        if (CollectionUtils.isNotEmpty(sorts)) {
            sorts.forEach(builder::withSort);
        }
        if (CollectionUtils.isNotEmpty(aggs)) {
            aggs.forEach(builder::addAggregation);
        }
        return builder.build();
    }

    public QueryBuilder getWhereCriteria() {
        String queryName = isQueryGoods ? "goodsInfos" : "goodsInfo";
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //仓库id
        if (Objects.nonNull(wareId)){
            boolQueryBuilder.must(termQuery(queryName.concat(".wareId"),wareId));
        }

        if (BooleanUtils.isTrue(marketMallQuery)){
            if (Objects.nonNull(marketId)){
                boolQueryBuilder.must(termQuery("marketId",marketId));
            }

            if (Objects.nonNull(mallId)){
                boolQueryBuilder.must(termQuery("mallId",mallId));
            }
        }

        //批量商品ID
        if (CollectionUtils.isNotEmpty(goodsIds)) {
            boolQueryBuilder.must(termsQuery(queryName.concat(".goodsId"), goodsIds));
        }
        //批量商品ID
        if (CollectionUtils.isNotEmpty(goodsIdsNot)) {
            boolQueryBuilder.mustNot(termsQuery(queryName.concat(".goodsId"), goodsIdsNot));
        }
        // 批量SKU编号
        if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
            boolQueryBuilder.must(termsQuery(queryName.concat(".goodsInfoId"), goodsInfoIds));
        }
        // 单个商品分类ID
        if (null != cateId) {
            boolQueryBuilder.must(termQuery("goodsCate.cateId", cateId));
        }

        //店铺ID
        if (storeId != null) {
            boolQueryBuilder.must(termQuery(queryName.concat(".storeId"), storeId));
        }

        //店铺IDs
        if (CollectionUtils.isNotEmpty(storeIds)){
            boolQueryBuilder.must(termsQuery(queryName.concat(".storeId"), storeIds));
        }

        //商品标识
        if (goodsInfoType != null) {
            if (goodsInfoType == 1) {
                boolQueryBuilder.must(termQuery(queryName.concat(".goodsInfoType"), 1));
            } else {
                boolQueryBuilder.mustNot(termQuery(queryName.concat(".goodsInfoType"), 1));
            }
        }
        //分仓限制
        if (null !=wareIdApp){
            BoolQueryBuilder stockBoolQuery = QueryBuilders.boolQuery();
            stockBoolQuery.must(
                    termQuery(queryName.concat(".goodsWareStockVOS.wareId"), wareIdApp)
            ).mustNot(termQuery(queryName.concat(".goodsWareStockVOS.stock"), 0));
            boolQueryBuilder.should(stockBoolQuery);
        }

        //模糊查询
        if (StringUtils.isNotBlank(likeGoodsName)) {
            boolQueryBuilder
                    //.should(matchQuery(queryName.concat(".goodsInfoName"),likeGoodsName))
                    .must(matchPhraseQuery("lowGoodsName", likeGoodsName));
        }
        if (StringUtils.isNotBlank(storeName)) {
            BoolQueryBuilder bq = QueryBuilders.boolQuery();
            bq.should(matchQuery(queryName.concat(".storeName"), storeName));
            boolQueryBuilder.must(bq);
        }
        if(StringUtils.isNotBlank(erpGoodsInfoNo)){
            BoolQueryBuilder bq = QueryBuilders.boolQuery();
            bq.should(matchQuery(queryName.concat(".erpGoodsInfoNo"), erpGoodsInfoNo));
            boolQueryBuilder.must(bq);
        }

        //模糊查询
        if (StringUtils.isNotBlank(keywords)) {
            BoolQueryBuilder bq = QueryBuilders.boolQuery();

            BoolQueryBuilder specBq = QueryBuilders.boolQuery();
            String[] t_keywords = StringUtils.split(keywords);
            //商品竞价 —— 关键词匹配
            if(Objects.nonNull(isKeywords) && isKeywords){
                bq.should(matchQuery(queryName.concat(".keyWords"), t_keywords[0]).boost(10f));
                bq.should(matchQuery(queryName.concat(".goodsInfoName"), t_keywords[0]).boost(2f));
            }else {
                for (String keyword : t_keywords) {
                    //优先条形码
                    bq.should(matchQuery(queryName.concat(".erpGoodsInfoNo"), keyword).boost(3f));
                    bq.should(matchQuery(queryName.concat(".goodsInfoName"), keyword).boost(2f));
                    //bq.should(matchPhrasePrefixQuery("goodsInfo.goodsInfoName",keyword).boost(2f));//主要解决不区分英文大小写、英文词根不匹配问题
                    bq.should(wildcardQuery("lowGoodsName", "*".concat(StringUtils.lowerCase(keyword)).concat("*")).boost(2f));
                    specBq.should(matchPhrasePrefixQuery("specDetails.detailName", keyword).boost(0.5f));
                    //店铺搜索不包含平台分类
                    if (storeId == null) {
                        bq.should(matchPhrasePrefixQuery("goodsCate.cateName", keyword).boost(1f));
                    }
                    bq.should(matchPhrasePrefixQuery("goodsBrand.brandName", keyword).boost(1f));
                }
            }
//            bq.should(hasParentQuery(EsConstants.DOC_CATE_BRAND_TYPE, catBrandBq, false));
            bq.should(nestedQuery("specDetails", specBq, ScoreMode.None));
            boolQueryBuilder.must(bq);
        }


        if (CollectionUtils.isNotEmpty(specs)) {
            BoolQueryBuilder bq = QueryBuilders.boolQuery();
            specs.stream().forEach(spec -> {
                List<String> values = spec.getValues();
                if (CollectionUtils.isNotEmpty(values)) {
                    BoolQueryBuilder bqv = QueryBuilders.boolQuery();
                    bqv.must(termQuery("specDetails.specName", spec.getName()));
                    bqv.must(termsQuery("specDetails.allDetailName", values));
                    bq.should(nestedQuery("specDetails", bqv, ScoreMode.None));
                }
            });
            boolQueryBuilder.must(bq);
        }

        //批量属性值ID
        if (CollectionUtils.isNotEmpty(propDetails)) {
            BoolQueryBuilder bq = QueryBuilders.boolQuery();
            propDetails.stream().forEach(prop -> {
                List<Long> values = prop.getDetailIds();
                if (CollectionUtils.isNotEmpty(values)) {
                    BoolQueryBuilder bqv = QueryBuilders.boolQuery();
                    // 某个属性prodId=1 && detailId in (1,2)
                    bqv.must(termQuery("propDetails.propId", prop.getPropId()));
                    bqv.must(termsQuery("propDetails.detailId", values));
                    // 多个不同属性(prodId=1 && detailId in (1,2)) 并且的关系
                    bq.must(nestedQuery("propDetails", bqv, ScoreMode.None));
                }
            });
            boolQueryBuilder.must(bq);
        }

        //批量商品分类ID
        if (CollectionUtils.isNotEmpty(brandIds)) {
            boolQueryBuilder.must(termsQuery("goodsBrand.brandId", brandIds));
        } else if (CollectionUtils.isNotEmpty(cateBindBrandIds) && sortByCateBrand && Objects.nonNull(cateId)) {
            //批量商品分类ID
            boolQueryBuilder.should(termsQuery("goodsBrand.brandId", brandIds));
        }

        //批量商品分类ID
        if (companyType != null) {
            boolQueryBuilder.must(termQuery(queryName.concat(".companyType"), companyType.intValue()));
        }

        //库存状态
        if (stockFlag != null) {
            if (Constants.yes.equals(stockFlag)) {
                boolQueryBuilder.must(rangeQuery(queryName.concat(".stock")).gt(12));
            } else {
                boolQueryBuilder.must(rangeQuery(queryName.concat(".stock")).lte(0));
            }
        }
        //删除标记
        if (delFlag != null) {
            boolQueryBuilder.must(termQuery(queryName.concat(".delFlag"), delFlag));
        }
        //上下架状态
        if (addedFlag != null) {
            boolQueryBuilder.must(termQuery(queryName.concat(".addedFlag"), addedFlag));
        }

        //上下架状态
        if (isScatteredQuantitative != null) {
            boolQueryBuilder.must(termQuery(queryName.concat(".isScatteredQuantitative"), isScatteredQuantitative));
        }

        //批量店铺分类ID
        if (CollectionUtils.isNotEmpty(storeCateIds)) {
            boolQueryBuilder.must(termsQuery("storeCateIds", storeCateIds));
        }

        //店铺状态
        if (storeState != null) {
            boolQueryBuilder.must(termQuery("storeState", storeState));
        }

        //禁售状态
        if (forbidStatus != null) {
            boolQueryBuilder.must(termQuery("forbidStatus", forbidStatus));
        }

        //审核状态
        if (auditStatus != null) {
            boolQueryBuilder.must(termQuery("auditStatus", auditStatus));
        }

        /**
         * 签约开始日期
         */
        if (StringUtils.isNotBlank(contractStartDate)) {
            boolQueryBuilder.must(rangeQuery("contractStartDate").lte(contractStartDate));
        }

        /**
         * 签约结束日期
         */
        if (StringUtils.isNotBlank(contractEndDate)) {
            boolQueryBuilder.must(rangeQuery("contractEndDate").gte(contractEndDate));
        }

        /**
         * 销商品审核状态 0:普通商品 1:待审核 2:已审核通过 3:审核不通过 4:禁止分销
         */
        if (distributionGoodsAudit != null) {
            boolQueryBuilder.must(termQuery(queryName.concat(".distributionGoodsAudit"), String.valueOf(distributionGoodsAudit)));
        }
        /**
         * 排除分销商品:排除(店铺分销开关开且商品审核通过)
         */
        if (excludeDistributionGoods) {
            BoolQueryBuilder bqv = QueryBuilders.boolQuery();
            // 某个属性prodId=1 && detailId in (1,2)
            bqv.must(termQuery(queryName.concat(".distributionGoodsAudit"), DistributionGoodsAudit.CHECKED.toValue()));
            bqv.must(termQuery("distributionGoodsStatus", DefaultFlag.NO.toValue()));
            boolQueryBuilder.mustNot(bqv);
        }

        if (distributionGoodsStatus != null) {
            boolQueryBuilder.must(termQuery("distributionGoodsStatus", distributionGoodsStatus));
        }

        //企业购商品过滤-企业购且非分销
        if (enterPriseGoodsStatus != null) {
            boolQueryBuilder.must(termQuery(queryName.concat(".enterPriseAuditStatus"), enterPriseGoodsStatus));
            if(Objects.equals(EnterpriseAuditState.CHECKED.toValue(),enterPriseGoodsStatus)){
                BoolQueryBuilder bqv = QueryBuilders.boolQuery();
                // 某个属性prodId=1 && detailId in (1,2)
                bqv.must(termQuery(queryName.concat(".distributionGoodsAudit"), DistributionGoodsAudit.CHECKED.toValue()));
                bqv.must(termQuery("distributionGoodsStatus", DefaultFlag.NO.toValue()));
                boolQueryBuilder.mustNot(bqv);
            }
        }

        if(hideSelectedDistributionGoods && CollectionUtils.isNotEmpty(distributionGoodsInfoIds)){
            boolQueryBuilder.mustNot(termsQuery(queryName.concat(".goodsInfoId"), distributionGoodsInfoIds));
        }

        System.out.println(String.format("ES商口查询条件->%s", boolQueryBuilder.toString()));
        return boolQueryBuilder;
    }

    /**
     * 填放排序参数
     *
     * @param fieldName 字段名
     * @param sort      排序
     */
    public void putSort(String fieldName, SortOrder sort) {
        if(fieldName.contains("brandSeqNum") || fieldName.contains("goodsSeqNum") || fieldName.contains("recommendSort")){
            sorts.add(new FieldSortBuilder(fieldName).unmappedType("long").order(sort));
        } else if ( fieldName.contains("brandRelSeqNum")) {
            sorts.add(new FieldSortBuilder(fieldName).unmappedType("long").order(sort).missing("_last"));
        }else{
            sorts.add(new FieldSortBuilder(fieldName).order(sort));
        }
    }

    /**
     * 按_score降序排列
     */
    public void putScoreSort() {
        sorts.add(new ScoreSortBuilder());
    }


    /**
     * 填放排序参数
     *
     * @param builder
     */
    public void putSort(SortBuilder builder) {
        sorts.add(builder);
    }

    /**
     * 填放聚合参数
     *
     * @param builder
     */
    public void putAgg(AbstractAggregationBuilder builder) {
        aggs.add(builder);
    }

}
