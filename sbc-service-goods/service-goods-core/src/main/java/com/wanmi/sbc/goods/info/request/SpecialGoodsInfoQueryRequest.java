package com.wanmi.sbc.goods.info.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.util.XssUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecialGoodsInfoQueryRequest  extends BaseQueryRequest {

    /**
     * 模糊条件-商品名称
     */
    private String likeGoodsName;

    /**
     * 模糊条件-SKU编码
     */
    private String likeGoodsInfoNo;

    /**
     * 批量SKU ID
     */
    private List<String> goodsInfoIds;
    /**
     * 平台类目-仅限三级类目
     */
    private Long cateId;

    /**
     * 品牌编号
     */
    private Long brandId;

    /**
     * 上下架状态
     */
    private Integer addedFlag;

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
     * 是否过滤商品状态为失效的商品  0 否 1 是
     * 商品状态 0：正常 1：缺货 2：失效
     */
    private Integer goodsStatus ;

    /**
     * 销售类型 0:批发, 1:零售
     */
    private Integer saleType;

    /**
     * 商品状态
     */
    private Integer goodsInfoType;

    /**
     * 删除标记
     */
    private Integer delFlag;


    /**
     * 是否分页
     */
    public boolean pageFlag;

//    public boolean isPageFlag() {
//        return pageFlag;
//    }
//
//    public void setPageFlag(boolean pageFlag) {
//        this.pageFlag = pageFlag;
//    }

    /**
     * 封装公共条件
     *
     * @return
     */
    public Specification<GoodsInfo> getWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //模糊查询名称
            if (StringUtils.isNotEmpty(likeGoodsName)) {
                predicates.add(cbuild.like(root.get("goodsInfoName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeGoodsName.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            //模糊查询SKU编码
            if (StringUtils.isNotEmpty(likeGoodsInfoNo)) {
                predicates.add(cbuild.like(root.get("goodsInfoNo"),
                        StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(likeGoodsInfoNo.trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            //批量SKU编号
            if(CollectionUtils.isNotEmpty(goodsInfoIds)){
                predicates.add(root.get("goodsInfoId").in(goodsInfoIds));
            }

//            //店铺ID
//            if(storeId != null){
//                predicates.add(cbuild.equal(root.get("storeId"), storeId));
//            }

            //查询品牌编号
            if (brandId != null && brandId > 0) {
                predicates.add(cbuild.equal(root.get("brandId"), brandId));
            }
            //查询商品分类编号
            if (cateId != null && cateId > 0) {
                predicates.add(cbuild.equal(root.get("cateId"), cateId));
            }

//            //批量查询商品分类编号
//            if (CollectionUtils.isNotEmpty(cateIds)) {
//                predicates.add(root.get("cateId").in(cateIds));
//            }
//
//            //spu id 集合
//            if (CollectionUtils.isNotEmpty(goodsIds)) {
//                predicates.add(root.get("goodsId").in(goodsIds));
//            }

            //上下架状态
            if (Objects.nonNull(addedFlag)){
                predicates.add(cbuild.equal(root.get("addedFlag"), addedFlag));
            }

            // 特价范围
            if(Objects.nonNull(specialPriceFirst)){
                //大于等于
                predicates.add(cbuild.ge(root.get("marketPrice"), specialPriceFirst));
            }
            if (Objects.nonNull(specialPriceLast)){
                //小于等于
                predicates.add(cbuild.le(root.get("marketPrice"), specialPriceLast));
            }

//            // 预估佣金范围
//            if(Objects.nonNull(distributionCommissionFirst)){
//                //大于等于
//                predicates.add(cbuild.ge(root.get("distributionCommission"), distributionCommissionFirst));
//            }
//            if (Objects.nonNull(distributionCommissionLast)){
//
//                predicates.add(cbuild.le(root.get("distributionCommission"), distributionCommissionLast));
//            }
//            // 佣金比例范围
//            if(Objects.nonNull(commissionRateFirst)){
//                //大于等于
//                predicates.add(cbuild.ge(root.get("commissionRate"), commissionRateFirst));
//            }
//            if (Objects.nonNull(commissionRateLast)){
//                //小于等于
//                predicates.add(cbuild.le(root.get("commissionRate"), commissionRateLast));
//            }
//            // 分销销量范围
//            if(Objects.nonNull(distributionSalesCountFirst)){
//                //大于等于
//                predicates.add(cbuild.ge(root.get("distributionSalesCount"), distributionSalesCountFirst));
//            }
//            if (Objects.nonNull(distributionSalesCountLast)){
//                //小于等于
//                predicates.add(cbuild.le(root.get("distributionSalesCount"), distributionSalesCountLast));
//            }
//
//            // 库存范围
//            if(Objects.nonNull(stockFirst)){
//                //大于等于
//                predicates.add(cbuild.ge(root.get("stock"), stockFirst));
//            }
//            if (Objects.nonNull(stockLast)){
//                //小于等于
//                predicates.add(cbuild.le(root.get("stock"), stockLast));
//            }
//
//            //分销商品审核状态
//            if(Objects.nonNull(distributionGoodsAudit)){
//                predicates.add(cbuild.equal(root.get("distributionGoodsAudit"), distributionGoodsAudit));
//            }
            //批次号
            if(Objects.nonNull(goodsInfoBatchNo)){
                predicates.add(cbuild.equal(root.get("goodsInfoBatchNo"), goodsInfoBatchNo));
            }

            //商品类型 0：普通商品 1：特价商品
            if(Objects.nonNull(goodsInfoType)){
                predicates.add(cbuild.equal(root.get("goodsInfoType"), goodsInfoType));
            }

            //是否过滤商品状态为失效的商品  0 否 1 是
            if (Objects.nonNull(goodsStatus) && goodsStatus == BoolFlag.YES.toValue()) {
                predicates.add(cbuild.equal(root.get("auditStatus"), CheckStatus.CHECKED.toValue()));
            }

            //销售类型
            if (!ObjectUtils.isEmpty(saleType)) {
                predicates.add(cbuild.equal(root.get("saleType"), saleType));
            }

            //删除标记
            if (delFlag != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), delFlag));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
