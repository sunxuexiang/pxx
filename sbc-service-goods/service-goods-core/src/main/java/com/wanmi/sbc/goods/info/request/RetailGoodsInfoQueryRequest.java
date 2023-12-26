package com.wanmi.sbc.goods.info.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;
import com.wanmi.sbc.goods.bean.enums.GoodsInfoSelectStatus;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.model.root.RetailGoodsInfo;
import com.wanmi.sbc.goods.util.XssUtils;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 商品SKU查询请求
 * Created by daiyitian on 2017/3/24.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RetailGoodsInfoQueryRequest extends BaseQueryRequest implements Serializable {

    private static final long serialVersionUID = 8229393304606738442L;
    /**
     * 批量SKU编号
     */
    private List<String> goodsInfoIds;

    /**
     * SPU编号
     */
    private String goodsId;

    /**
     * 批量SPU编号
     */
    private List<String> goodsIds;

    /**
     *品牌编号
     */
    private Long brandId;

    /**
     * 批量品牌编号
     */
    private List<Long> brandIds;

    /**
     * 分类编号
     */
    private Long cateId;

    /**
     * 批量分类编号
     */
    private List<Long> cateIds;

    /**
     * 店铺分类id
     */
    private Long storeCateId;

    /**
     * 模糊条件-商品名称
     */
    private String likeGoodsName;

    /**
     * 精确条件-批量SKU编码
     */
    private List<String> goodsInfoNos;

    /**
     * 模糊条件-SKU编码
     */
    private String likeGoodsInfoNo;

    /**
     * 模糊条件-SPU编码
     */
    private String likeGoodsNo;

    /**
     * 上下架状态
     */
    private Integer addedFlag;

    /**
     * 上下架状态-批量
     */
    private List<Integer> addedFlags;

    /**
     * 删除标记
     */
    private Integer delFlag;

    /**
     * 客户编号
     */
    private String customerId;

    /**
     * 客户等级
     */
    private Long customerLevelId;

    /**
     * 客户等级折扣
     */
    private BigDecimal customerLevelDiscount;

    /**
     * 非GoodsId
     */
    private String notGoodsId;

    /**
     * 非GoodsInfoId
     */
    private String notGoodsInfoId;

    /**
     * 公司信息ID
     */
    private Long companyInfoId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 批量店铺ID
     */
    private List<Long> storeIds;

    /**
     * 审核状态
     */
    private CheckStatus auditStatus;

    /**
     * 审核状态
     */
    private List<CheckStatus> auditStatuses;

    /**
     * 关键词，目前范围：商品名称、SKU编码
     */
    private String keyword;

    /**
     * 业务员app,商品状态筛选
     */
    private List<GoodsInfoSelectStatus> goodsSelectStatuses;

    /**
     * 商家类型
     */
    private CompanyType companyType;

    /**
     * 销售类别
     */
    private Integer saleType;

    /**
     * 企业购商品审核状态
     */
    private EnterpriseAuditState enterPriseAuditState;

    /**
     * 批量供应商商品SKU编号
     */
    private List<String> providerGoodsInfoIds;

    /**
     * 商品来源，0供应商，1商家
     */
    private Integer goodsSource;

    /**
     * 模糊查询erp的NO
     */
    private String likeErpNo;

    /**
     * 特价商品的标识（0非特价  1特价）
     */
    private Integer goodsInfoType;

    private Long wareId;

    //三期商家端商品列表查询更改：新增查询条件
    /**
     * 特价范围参数1
     */
    private BigDecimal specialPriceFirst;

    /**
     * 特价范围参数2
     */
    private BigDecimal specialPriceLast;

    /**
     * 批次号
     */
    private String goodsInfoBatchNo;

    /**
     * 是否为囤货 1：囤货中 ，2：已囤完 ，3：全部
     */
    private Long stockUp;

    /**
     * 关联本品skuId
     */
    private String choseProductSkuId;

    /**
     * 封装公共条件
     *
     * @return
     */
    public Specification<RetailGoodsInfo> getWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //批量SKU编号
            if(CollectionUtils.isNotEmpty(goodsInfoIds)){
                predicates.add(root.get("goodsInfoId").in(goodsInfoIds));
            }
            //SPU编号
            if(StringUtils.isNotBlank(goodsId)){
                predicates.add(cbuild.equal(root.get("goodsId"), goodsId));
            }
            //批量SKU编号
            if(CollectionUtils.isNotEmpty(goodsInfoNos)){
                predicates.add(root.get("goodsInfoNo").in(goodsInfoNos));
            }
            //批量SPU编号
            if(CollectionUtils.isNotEmpty(goodsIds)){
                predicates.add(root.get("goodsId").in(goodsIds));
            }
            //SKU编码
            if(StringUtils.isNotEmpty(likeGoodsInfoNo)){
                predicates.add(cbuild.like(root.get("goodsInfoNo"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeGoodsInfoNo.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //erp编码
            if(StringUtils.isNotEmpty(likeErpNo)){
                predicates.add(cbuild.like(root.get("erpGoodsInfoNo"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeErpNo.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //店铺ID
            if(storeId != null){
                predicates.add(cbuild.equal(root.get("storeId"), storeId));
            }
            //公司信息ID
            if(companyInfoId != null){
                predicates.add(cbuild.equal(root.get("companyInfoId"), companyInfoId));
            }
            //批量店铺ID
            if(CollectionUtils.isNotEmpty(storeIds)){
                predicates.add(root.get("storeId").in(storeIds));
            }
            //模糊查询名称
            if(StringUtils.isNotEmpty(likeGoodsName)){
                predicates.add(cbuild.like(root.get("goodsInfoName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeGoodsName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //关键字搜索
            if (StringUtils.isNotBlank(keyword)) {
                String str = StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(keyword.trim())).concat(StringUtil.SQL_LIKE_CHAR);
                predicates.add(cbuild.or(cbuild.like(root.get("goodsInfoName"), str), cbuild.like(root.get("goodsInfoNo"), str)));
            }
            //上下架状态
            if (addedFlag != null) {
                predicates.add(cbuild.equal(root.get("addedFlag"), addedFlag));
            }
            //多个上下架状态
            if (CollectionUtils.isNotEmpty(addedFlags)) {
                predicates.add(root.get("addedFlag").in(addedFlags));
            }
            //审核状态
            if(auditStatus != null){
                predicates.add(cbuild.equal(root.get("auditStatus"), auditStatus));
            }
            //多个审核状态
            if(CollectionUtils.isNotEmpty(auditStatuses)){
                predicates.add(root.get("auditStatus").in(auditStatuses));
            }
            //删除标记
            if (delFlag != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), delFlag));
            }
            //非商品编号
            if(StringUtils.isNotBlank(notGoodsId)){
                predicates.add(cbuild.notEqual(root.get("goodsId"), notGoodsId));
            }
            //非商品SKU编号
            if(StringUtils.isNotBlank(notGoodsInfoId)){
                predicates.add(cbuild.notEqual(root.get("goodsInfoId"), notGoodsInfoId));
            }

            // 商家类型
            if(Objects.nonNull(companyType)){
                predicates.add(cbuild.equal(root.get("companyType"), companyType.toValue()));
            }

            //批量供应商商品SKU编号
            if(CollectionUtils.isNotEmpty(providerGoodsInfoIds)){
                predicates.add(root.get("providerGoodsInfoId").in(providerGoodsInfoIds));
            }

            //业务员app商品状态筛选
            if(CollectionUtils.isNotEmpty(goodsSelectStatuses)){
                List<Predicate> orPredicate = new ArrayList<>();
                goodsSelectStatuses.forEach(goodsInfoSelectStatus -> {
                    if(goodsInfoSelectStatus != null){
                        if(goodsInfoSelectStatus == GoodsInfoSelectStatus.ADDED){
                            orPredicate.add(cbuild.and(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED), cbuild.equal(root.get("addedFlag"), AddedFlag.YES.toValue())));
                        } else if(goodsInfoSelectStatus == GoodsInfoSelectStatus.NOT_ADDED){
                            orPredicate.add(cbuild.and(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED), cbuild.equal(root.get("addedFlag"), AddedFlag.NO.toValue())));
                        } else if(goodsInfoSelectStatus == GoodsInfoSelectStatus.OTHER){
                            orPredicate.add(root.get("auditStatus").in(CheckStatus.FORBADE, CheckStatus.NOT_PASS, CheckStatus.WAIT_CHECK));
                        }
                    }
                });
                predicates.add(cbuild.or(orPredicate.toArray(new Predicate[orPredicate.size()])));
            }

            if (saleType != null){
                Join<Goods, GoodsInfo> join = root.join("goods", JoinType.LEFT);
                predicates.add(cbuild.equal(join.get("saleType"), saleType));
            }

            /**
             * 企业购商品的审核状态
             */
            if(enterPriseAuditState != null && !EnterpriseAuditState.INIT.equals(enterPriseAuditState)){
                predicates.add(cbuild.equal(root.get("enterPriseAuditState"), enterPriseAuditState.toValue()));
            }

            // 商品来源
            if(Objects.nonNull(goodsSource)){
                predicates.add(cbuild.equal(root.get("goodsSource"), goodsSource));
            }

            /**
             * 是否为特价的标识
             */
            if(Objects.nonNull(goodsInfoType)){
                predicates.add(cbuild.equal(root.get("goodsInfoType"), goodsInfoType));
            }

            //三期商家端商品列表查询更改：新增查询条件
            /**
             * 特价范围
             */
            if(Objects.nonNull(specialPriceFirst)){
                //大于等于
                predicates.add(cbuild.ge(root.get("marketPrice"), specialPriceFirst));
            }
            if (Objects.nonNull(specialPriceLast)){
                //小于等于
                predicates.add(cbuild.le(root.get("marketPrice"), specialPriceLast));
            }
            /**
             * 批次号
             */
            if(StringUtils.isNotBlank(goodsInfoBatchNo)){
                predicates.add(cbuild.equal(root.get("goodsInfoBatchNo"), goodsInfoBatchNo));
                predicates.add(cbuild.equal(root.get("goodsInfoType"), 1));
            }

            /**
             * 关联本品skuId
             */
            if (Objects.nonNull(choseProductSkuId)) {
                predicates.add(cbuild.equal(root.get("choseProductSkuId"),choseProductSkuId));
            }

            /**
             * 搜索是否为囤货 1：囤货中 ，2：已囤完
             */
            if (stockUp != null && stockUp == 1){
                predicates.add(cbuild.ge(root.get("marketingId"),1L));
                predicates.add(cbuild.ge(root.get("purchaseNum"),1L));
            }
            if (stockUp != null && stockUp == 2){
                predicates.add(cbuild.ge(root.get("marketingId"),1L));
                predicates.add(cbuild.le(root.get("purchaseNum"),0L));
            }
            if (stockUp != null && stockUp == 3){
                predicates.add(cbuild.ge(root.get("marketingId"),1L));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }


    /**
     * 封装公共条件 零售
     *
     * @return
     */
    public Specification<RetailGoodsInfo> getRetailWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //批量SKU编号
            if(CollectionUtils.isNotEmpty(goodsInfoIds)){
                predicates.add(root.get("goodsInfoId").in(goodsInfoIds));
            }
            //SPU编号
            if(StringUtils.isNotBlank(goodsId)){
                predicates.add(cbuild.equal(root.get("goodsId"), goodsId));
            }
            //批量SKU编号
            if(CollectionUtils.isNotEmpty(goodsInfoNos)){
                predicates.add(root.get("goodsInfoNo").in(goodsInfoNos));
            }
            //批量SPU编号
            if(CollectionUtils.isNotEmpty(goodsIds)){
                predicates.add(root.get("goodsId").in(goodsIds));
            }
            //SKU编码
            if(StringUtils.isNotEmpty(likeGoodsInfoNo)){
                predicates.add(cbuild.like(root.get("goodsInfoNo"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeGoodsInfoNo.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //erp编码
            if(StringUtils.isNotEmpty(likeErpNo)){
                predicates.add(cbuild.like(root.get("erpGoodsInfoNo"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeErpNo.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //店铺ID
            if(storeId != null){
                predicates.add(cbuild.equal(root.get("storeId"), storeId));
            }
            //公司信息ID
            if(companyInfoId != null){
                predicates.add(cbuild.equal(root.get("companyInfoId"), companyInfoId));
            }
            //批量店铺ID
            if(CollectionUtils.isNotEmpty(storeIds)){
                predicates.add(root.get("storeId").in(storeIds));
            }
            //模糊查询名称
            if(StringUtils.isNotEmpty(likeGoodsName)){
                predicates.add(cbuild.like(root.get("goodsInfoName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeGoodsName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }

            //关键字搜索
            if (StringUtils.isNotBlank(keyword)) {
                String str = StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(keyword.trim())).concat(StringUtil.SQL_LIKE_CHAR);
                predicates.add(cbuild.or(cbuild.like(root.get("goodsInfoName"), str), cbuild.like(root.get("goodsInfoNo"), str)));
            }
            //上下架状态
            if (addedFlag != null) {
                predicates.add(cbuild.equal(root.get("addedFlag"), addedFlag));
            }
            //多个上下架状态
            if (CollectionUtils.isNotEmpty(addedFlags)) {
                predicates.add(root.get("addedFlag").in(addedFlags));
            }
            //审核状态
            if(auditStatus != null){
                predicates.add(cbuild.equal(root.get("auditStatus"), auditStatus));
            }
            //多个审核状态
            if(CollectionUtils.isNotEmpty(auditStatuses)){
                predicates.add(root.get("auditStatus").in(auditStatuses));
            }
            //删除标记
            if (delFlag != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), delFlag));
            }
            //非商品编号
            if(StringUtils.isNotBlank(notGoodsId)){
                predicates.add(cbuild.notEqual(root.get("goodsId"), notGoodsId));
            }
            //非商品SKU编号
            if(StringUtils.isNotBlank(notGoodsInfoId)){
                predicates.add(cbuild.notEqual(root.get("goodsInfoId"), notGoodsInfoId));
            }

            // 商家类型
            if(Objects.nonNull(companyType)){
                predicates.add(cbuild.equal(root.get("companyType"), companyType.toValue()));
            }

            //批量供应商商品SKU编号
            if(CollectionUtils.isNotEmpty(providerGoodsInfoIds)){
                predicates.add(root.get("providerGoodsInfoId").in(providerGoodsInfoIds));
            }

            //业务员app商品状态筛选
            if(CollectionUtils.isNotEmpty(goodsSelectStatuses)){
                List<Predicate> orPredicate = new ArrayList<>();
                goodsSelectStatuses.forEach(goodsInfoSelectStatus -> {
                    if(goodsInfoSelectStatus != null){
                        if(goodsInfoSelectStatus == GoodsInfoSelectStatus.ADDED){
                            orPredicate.add(cbuild.and(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED), cbuild.equal(root.get("addedFlag"), AddedFlag.YES.toValue())));
                        } else if(goodsInfoSelectStatus == GoodsInfoSelectStatus.NOT_ADDED){
                            orPredicate.add(cbuild.and(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED), cbuild.equal(root.get("addedFlag"), AddedFlag.NO.toValue())));
                        } else if(goodsInfoSelectStatus == GoodsInfoSelectStatus.OTHER){
                            orPredicate.add(root.get("auditStatus").in(CheckStatus.FORBADE, CheckStatus.NOT_PASS, CheckStatus.WAIT_CHECK));
                        }
                    }
                });
                predicates.add(cbuild.or(orPredicate.toArray(new Predicate[orPredicate.size()])));
            }

            if (saleType != null){
                Join<Goods, GoodsInfo> join = root.join("goods", JoinType.LEFT);
                predicates.add(cbuild.equal(join.get("saleType"), saleType));
            }

            /**
             * 企业购商品的审核状态
             */
            if(enterPriseAuditState != null && !EnterpriseAuditState.INIT.equals(enterPriseAuditState)){
                predicates.add(cbuild.equal(root.get("enterPriseAuditState"), enterPriseAuditState.toValue()));
            }

            // 商品来源
            if(Objects.nonNull(goodsSource)){
                predicates.add(cbuild.equal(root.get("goodsSource"), goodsSource));
            }

            /**
             * 是否为特价的标识
             */
            if(Objects.nonNull(goodsInfoType)){
                predicates.add(cbuild.equal(root.get("goodsInfoType"), goodsInfoType));
            }

            //三期商家端商品列表查询更改：新增查询条件
            /**
             * 特价范围
             */
            if(Objects.nonNull(specialPriceFirst)){
                //大于等于
                predicates.add(cbuild.ge(root.get("marketPrice"), specialPriceFirst));
            }
            if (Objects.nonNull(specialPriceLast)){
                //小于等于
                predicates.add(cbuild.le(root.get("marketPrice"), specialPriceLast));
            }
            /**
             * 批次号
             */
            if(StringUtils.isNotBlank(goodsInfoBatchNo)){
                predicates.add(cbuild.equal(root.get("goodsInfoBatchNo"), goodsInfoBatchNo));
                predicates.add(cbuild.equal(root.get("goodsInfoType"), 1));
            }

            /**
             * 关联本品skuId
             */
            if (Objects.nonNull(choseProductSkuId)) {
                predicates.add(cbuild.equal(root.get("choseProductSkuId"),choseProductSkuId));
            }

            /**
             * 搜索是否为囤货 1：囤货中 ，2：已囤完
             */
            if (stockUp != null && stockUp == 1){
                predicates.add(cbuild.ge(root.get("marketingId"),1L));
                predicates.add(cbuild.ge(root.get("purchaseNum"),1L));
            }
            if (stockUp != null && stockUp == 2){
                predicates.add(cbuild.ge(root.get("marketingId"),1L));
                predicates.add(cbuild.le(root.get("purchaseNum"),0L));
            }
            if (stockUp != null && stockUp == 3){
                predicates.add(cbuild.ge(root.get("marketingId"),1L));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
